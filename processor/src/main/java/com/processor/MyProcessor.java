package com.processor;

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

@SupportedAnnotationTypes("com.processor.Net")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class MyProcessor extends AbstractProcessor{
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
                    .append("import com.fastlib.net.SimpleListener;").append('\n').append('\n');

            sb.append("public class ").append(className).append("{").append("\n");
            for(Element m:methods){
                if("<init>".equals(m.getSimpleName().toString())) continue;

                Url urlAnno=m.getAnnotation(Url.class);
                String methodName=m.getSimpleName().toString();
                String methodSuffix="Callback";
                StringBuilder paramsSb=new StringBuilder();
                ExecutableType type= (ExecutableType) m.asType();
                try {
                    Field methodParamsField=m.getClass().getDeclaredField("params");
                    List<VariableElement> list= (List<VariableElement>) methodParamsField.get(m);
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
                String url=urlAnno==null?"":urlAnno.value();
                sb.append(")").append("{").append("\n");
                sb.append("\t\t").append("Request request=new Request(\"").append(url).append('"').append(')').append('\n')
                        .append(paramsSb);
                sb.append("\t\t\t").append(".setListener(new SimpleListener<").append(type.getReturnType()).append('>').append("(){").append('\n')
                        .append("\t\t\t\t").append("@Override").append('\n')
                        .append("\t\t\t\t").append("public void onResponseListener(Request r,").append(type.getReturnType()).append(' ').append("result){").append('\n')
                        .append("\t\t\t\t\t").append(methodName).append(methodSuffix).append("(result);")
                        .append("\t\t\t\t").append("}\n")
                        .append("\t\t\t\t").append("});\n")
                        .append("\t\t").append("request.start();").append('\n')
                        .append("\t\t").append("return null;").append('\n')
                        .append('\t').append("}").append('\n');

                //callback segment
                sb.append("\t").append("public void ").append(methodName).append(methodSuffix).append("(")
                        .append(type.getReturnType()).append(' ').append("result").append("){};").append("\n")
                        .append('}').append('\n').append('\n');
            }
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