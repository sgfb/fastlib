package com.fastlib.net;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
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

@SupportedAnnotationTypes("com.fastlib.net.Net")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class NetRequestProcessor extends AbstractProcessor{
    private Elements mElementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mElementUtils=processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment){
        StringBuilder sb=new StringBuilder();
        Set<? extends Element> classElement=roundEnvironment.getElementsAnnotatedWith(Net.class);

        if(classElement==null||classElement.isEmpty()) return true;
        for(Element e:classElement){
            String className=e.getSimpleName().toString()+"_G";
            List<? extends Element> methods=e.getEnclosedElements();
            String packageName=mElementUtils.getPackageOf(e).getQualifiedName().toString();

            System.out.println("class "+className);
            sb.append("package ").append(packageName).append(';').append("\n");
            sb.append("import com.fastlib.net.Request;").append('\n')
                    .append("import com.fastlib.net.mock.MockProcess;").append('\n')
                    .append("import com.fastlib.net.listener.SimpleListener;").append('\n');

            sb.append("public class ").append(className).append(' ').append("implements ").append(e.getSimpleName()).append("{").append('\n');
            for(Element m:methods){
                if("<init>".equals(m.getSimpleName().toString())) continue;

                UrlWithMethod urlWithMethodAnno =m.getAnnotation(UrlWithMethod.class);
                NetMock mockAnno=m.getAnnotation(NetMock.class);
                String mockName=mockAnno==null?null:mockAnno.value();
                String methodName=m.getSimpleName().toString();
                String methodSuffix="Callback";
                StringBuilder paramsSb=new StringBuilder();
                StringBuilder mockSb=new StringBuilder();
                ExecutableType type= (ExecutableType) m.asType();
                String listenerName=methodName+"Listener";
                String listenerDeclare="AppListener<"+type.getReturnType()+">";

                if(mockName!=null){
                    mockSb.append("try{")
                            .append("request").append(".setMock(")
                            .append("(MockProcess)Class.forName(\"").append(mockName).append("\").newInstance());").append('\n')
                            .append("\t\t\t}catch(Exception e){e.printStackTrace();}").append('\n');
                }
                try {
                    Field methodParamsField=m.getClass().getDeclaredField("params");
                    List<VariableElement> list= (List<VariableElement>) methodParamsField.get(m);

                    //callback declare
                    sb.append('\t').append(listenerDeclare).append(' ').append(listenerName).append(';').append('\n');

                    //set callback method
                    sb.append('\t').append("public void set").append(methodName.substring(0,1).toUpperCase()).append(methodName.substring(1)).append("Listener(").append(listenerDeclare).append(" listener){")
                            .append(listenerName).append("=listener;").append("}\n");
                    sb.append('\t').append("public ").append(type.getReturnType()).append(" ").append(methodName).append("(");
                    for(int i=0;i<type.getParameterTypes().size();i++){
                        TypeMirror tm=type.getParameterTypes().get(i);
                        VariableElement paramElement=list.get(i);

                        sb.append(tm).append(" ").append(paramElement.getSimpleName()).append(",");
                        paramsSb.append("\t\t\t")
                                .append(".put(").append('"').append(paramElement.getSimpleName()).append('"').append(",")
                                .append(paramElement.getSimpleName()).append(")").append('\n');
                    }

                    if(type.getParameterTypes().size()>0)
                        sb.deleteCharAt(sb.length()-1);
                } catch (NoSuchFieldException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                }
                String url= urlWithMethodAnno ==null?"": urlWithMethodAnno.value();
                sb.append(")").append("{").append("\n");
                sb.append("\t\t").append("Request request=new Request(\"").append(url).append('"').append(')').append('\n')
                        .append(paramsSb);
                sb.append("\t\t\t").append(".setListener(new SimpleListener<").append(type.getReturnType()).append('>').append("(){").append('\n')
                        .append("\t\t\t\t").append("@Override").append('\n')
                        .append("\t\t\t\t").append("public void onResponseListener(Request r,").append(type.getReturnType()).append(' ').append("result){").append('\n')
                        .append("\t\t\t\t\t").append(methodName).append(methodSuffix).append("(result);")
                        .append("\t\t\t\t").append("}\n")
                        .append("\t\t\t\t").append("});\n")
                        .append("\t\t").append(mockSb)
                        .append("\t\t").append("request.start();").append('\n')
                        .append("\t\t").append("return null;").append('\n')
                        .append('\t').append("}").append('\n');

                //callback segment
                sb.append("\t").append("public void ").append(methodName).append(methodSuffix).append("(")
                        .append(type.getReturnType()).append(' ').append("result").append("){")
                        .append("if(").append(listenerName).append("!=null)").append(' ')
                        .append(listenerName).append(".success(result);")
                        .append('}')
                        .append("\n");
            }
            if(sb.length()>0)
                sb.append('}').append('\n').append('\n');
            try {
                Writer writer=processingEnv.getFiler().createSourceFile(packageName+"."+className,classElement.iterator().next()).openWriter();
                writer.append(sb.toString());
                writer.flush();
                writer.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return true;
    }
}