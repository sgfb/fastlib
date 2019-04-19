package com.fastlib.net;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

/**
 * Created by sgfb on 2018/4/26.
 * 网络接口辅助处理过程.具体功能见{@link Net}
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("com.fastlib.net.Net")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class NetRequestProcessor extends AbstractProcessor {
    private Elements mElementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mElementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> classElement = roundEnvironment.getElementsAnnotatedWith(Net.class);

        if (classElement == null || classElement.isEmpty()) return true;
        for (Element e : classElement) {
            String className = e.getSimpleName().toString() + "_G";
            List<? extends Element> elements = e.getEnclosedElements();
            String packageName = mElementUtils.getPackageOf(e).getQualifiedName().toString();
            StringBuilder classSb = new StringBuilder();

            //head define
            classSb.append("package ").append(packageName).append(';').append("\n\n");
            classSb.append("import com.fastlib.net.Request;").append('\n')
                    .append("import com.fastlib.net.mock.MockProcess;").append('\n')
                    .append("import com.fastlib.net.GenRequestInterceptor;").append('\n')
                    .append("import com.fastlib.net.listener.Listener;").append("\n\n");

            //fix codes
            classSb.append("public class ").append(className).append(' ').append("implements ").append(e.getSimpleName()).append("{").append('\n');
            classSb.append("\t").append("GenRequestInterceptor<Request> mInterceptor;").append("\n\n")
                    .append("\t").append("public ").append(className).append("(){}").append("\n\n")
                    .append("\t").append("public ").append(className).append("(GenRequestInterceptor<Request> interceptor){").append("\n")
                    .append("\t\t").append("mInterceptor=interceptor;").append("\n")
                    .append("\t").append("}").append("\n\n")
                    .append("\t").append("public ").append(className).append("(final com.fastlib.app.module.ModuleLife moduleLife){").append("\n")
                    .append("\t\t").append("mInterceptor=new GenRequestInterceptor<Request>() {").append("\n")
                    .append("\t\t\t").append("@Override").append("\n")
                    .append("\t\t\t").append("public void genCompleteBefore(Request request) {").append("\n")
                    .append("\t\t\t\t").append("request.setHostLifecycle(moduleLife);").append("\n")
                    .append("\t\t").append("}};").append("\n")
                    .append("\t").append("}").append("\n\n");
            for (Element element : elements) {
                if ("<init>".equals(element.getSimpleName().toString())) continue;
                if(!"com.sun.tools.javac.code.Symbol.MethodSymbol".equals(element.getClass().getCanonicalName())) continue;

                BaseParam baseParamAnno = element.getAnnotation(BaseParam.class);
                NetMock mockAnno = element.getAnnotation(NetMock.class);
                ExecutableType type = (ExecutableType) element.asType();
                String returnType=type.getReturnType().toString();
                String mockName = mockAnno == null ? null : mockAnno.value();
                String methodName = element.getSimpleName().toString();
                String methodSuffix = "Callback";
                String requestDefine =baseParamAnno==null?"Request": baseParamAnno.customRequest().isEmpty()? "Request" : baseParamAnno.customRequest();
                String customRootAddress=baseParamAnno==null?null:baseParamAnno.customRootAddress();
                String url = baseParamAnno == null ? "" : baseParamAnno.url();
                String requestMethod = baseParamAnno == null ? "post" : baseParamAnno.method();
                StringBuilder paramsSb = new StringBuilder();         //paramType paramName,paramType2 paramsName2
                StringBuilder mockSb = new StringBuilder();
                StringBuilder genRequestSb = new StringBuilder();     //genXXXRequest(param1,params2){...}
                StringBuilder listenerDeclare = new StringBuilder();  //Listener<Type,Object,Object>
                List<String> paramNameList = new ArrayList<>();

                listenerDeclare.append("Listener<").append(returnType).append(",Object,Object>");
                if (mockName != null) {
                    mockSb.append("\t\t").append("try{").append("\n")
                            .append("\t\t\t").append("request").append(".setMock(")
                            .append("(MockProcess)Class.forName(\"").append(mockName).append("\").newInstance());").append('\n')
                            .append("\t\t}catch(Exception e){e.printStackTrace();}").append('\n');
                }
                try {
                    Field methodParamsField = element.getClass().getDeclaredField("params");
                    List<VariableElement> list = (List<VariableElement>) methodParamsField.get(element);

                    //loop params
                    for (int i = 0; i < type.getParameterTypes().size(); i++) {
                        TypeMirror tm = type.getParameterTypes().get(i);
                        VariableElement paramElement = list.get(i);

                        paramNameList.add(paramElement.getSimpleName().toString());
                        paramsSb.append(tm).append(" ").append(paramElement.getSimpleName()).append(",");
                    }

                    //delete last "," character
                    if (paramsSb.length() > 0)
                        paramsSb.deleteCharAt(paramsSb.length()-1);
                    genRequestSb.append("\t").append("public ").append(requestDefine).append(" gen").append(methodName.substring(0, 1).toUpperCase()).append(methodName.substring(1))
                            .append("Request(").append(paramsSb).append(paramsSb.length()==0?"":",").append(listenerDeclare).append(" listener").append(")").append("{").append("\n")
                            .append("\t\t").append(requestDefine).append(" request=new ").append(requestDefine).append("(")
                            .append('"').append(requestMethod).append('"').append(",").append('"').append(url).append('"').append(")").append("\n")
                            .append("\t\t\t").append(".setListener(listener)").append("\n");
                    if(customRootAddress!=null&&customRootAddress.length()>0)
                        genRequestSb.append("\t\t\t").append(".setCustomRootAddress(").append("\"").append(customRootAddress).append("\"").append(")").append("\n");
                    for (String paramName : paramNameList) {
                        genRequestSb.append("\t\t\t").append(".put(").append('"').append(paramName).append('"').append(",").append(paramName).append(")\n");
                    }
                    genRequestSb.replace(genRequestSb.length()-1,genRequestSb.length()-1,";\n");
                    if(mockSb.length()>0){
                        genRequestSb.append(mockSb).append("\n");
                    }
                    genRequestSb.append("\t\t").append("if(mInterceptor!=null)").append("\n")
                            .append("\t\t\t").append("mInterceptor").append(".genCompleteBefore(request);").append("\n")
                            .append("\t\t").append("return request;").append("\n")
                            .append("\t").append("}").append("\n\n");
                } catch (NoSuchFieldException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                }
                classSb.append(genRequestSb);

                //no listener overloading method by launcher request
                classSb.append("\t").append("public ").append(returnType).append(" ").append(methodName).append("(")
                        .append(paramsSb).append("){").append("\n")
                        .append("\t\t").append(methodName).append("(");
                for(String paramName:paramNameList)
                    classSb.append(paramName).append(",");
                classSb.append("null);").append("\n")
                        .append("\t\t").append("return null;").append("\n")
                        .append("\t").append("}").append("\n\n");

                //standard launcher request method
                classSb.append("\t").append("public ").append(returnType).append(" ").append(methodName).append("(")
                        .append(paramsSb).append(paramsSb.length()==0?"":",").append("final ").append(listenerDeclare).append(" listener)").append("{").append("\n")
                        .append("\t\t").append(requestDefine).append(" request=gen").append(methodName.substring(0, 1).toUpperCase()).append(methodName.substring(1))
                        .append("Request").append("(");
                for(String paramName:paramNameList)
                    classSb.append(paramName).append(",");
                classSb.append("\t").append("listener);").append("\n")
                        .append("\t\t").append("if(listener!=null){").append("\n")
                        .append("\t\t\t").append("request.setListener(new Listener<").append(returnType).append(",Object,Object>(){").append("\n").append("\n")
                        .append("\t\t\t\t").append("@Override").append("\n")
                        .append("\t\t\t\t").append("public void onRawData(").append(requestDefine).append(" r,byte[] data){").append("\n")
                        .append("\t\t\t\t\t").append("if(listener!=null) listener.onRawData(r,data);").append("\n")
                        .append("\t\t\t\t").append("}").append("\n\n")
                        .append("\t\t\t\t").append("@Override").append("\n")
                        .append("\t\t\t\t").append("public void onTranslateJson(").append(requestDefine).append(" r,String json){").append("\n")
                        .append("\t\t\t\t\t").append("if(listener!=null) listener.onTranslateJson(r,json);").append("\n")
                        .append("\t\t\t\t").append("}").append("\n\n")
                        .append("\t\t\t\t").append("@Override").append("\n")
                        .append("\t\t\t\t").append("public void onResponseListener(").append(requestDefine).append(" r,").append(returnType)
                        .append(" result,Object result2,Object cookedResult){").append("\n")
                        .append("\t\t\t\t\t").append(methodName).append(methodSuffix).append("(r,result);").append("\n")
                        .append("\t\t\t\t\t").append("if(listener!=null) listener.onResponseListener(r,result,result2,cookedResult);").append("\n")
                        .append("\t\t\t\t").append("}").append("\n\n")
                        .append("\t\t\t\t").append("@Override").append("\n")
                        .append("\t\t\t\t").append("public void onErrorListener(").append(requestDefine).append(" r,Exception error){").append("\n")
                        .append("\t\t\t\t\t").append("if(listener!=null) listener.onErrorListener(r,error);").append("\n")
                        .append("\t\t\t\t").append("}").append("\n")
                        .append("\t\t\t").append("});").append("\n")
                        .append("\t\t").append("}").append("\n");

                classSb.append("\t\t").append("request.start();").append("\n")
                        .append("\t\t").append("return null;").append("\n")
                        .append("\t").append("}").append("\n\n");

                //callback segment
                classSb.append("\t").append("public void ").append(methodName).append(methodSuffix).append("(")
                        .append(requestDefine).append(" r").append(",").append(returnType).append(" result").append("){}").append("\n");
            }
            classSb.append('}').append('\n');

            //generator and write to file
            try {
                Writer writer = processingEnv.getFiler().createSourceFile(packageName + "." + className, classElement.iterator().next()).openWriter();
                writer.write(classSb.toString());
                writer.flush();
                writer.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return true;
    }
}