package com.processor;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;

@SupportedAnnotationTypes("com.processor.Net")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class MyProcessor extends AbstractProcessor{

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment){
        System.out.println("start process");
        StringBuilder sb=new StringBuilder();
        Set<? extends Element> classElement=roundEnvironment.getElementsAnnotatedWith(Net.class);

        if(classElement==null||classElement.isEmpty()) return true;
        for(Element e:classElement){
            System.out.println("class "+e.getSimpleName());
            List<? extends Element> methods=e.getEnclosedElements();

            sb.append("package com.fastlib;").append("\n");
            sb.append("public class ").append(e.getSimpleName()).append("{").append("\n");

            for(Element m:methods){
                if("<init>".equals(m.getSimpleName().toString())) continue;

                StringBuilder paramsSb=new StringBuilder();
                ExecutableType type= (ExecutableType) m.asType();
                try {
                    Field methodParamsField=m.getClass().getDeclaredField("params");
                    List<VariableElement> list= (List<VariableElement>) methodParamsField.get(m);
                    sb.append("public ").append(type.getReturnType()).append(" ").append(m.getSimpleName()).append("(");
                    for(int i=0;i<type.getParameterTypes().size();i++){
                        TypeMirror tm=type.getParameterTypes().get(i);
                        VariableElement paramElement=list.get(i);

                        sb.append(tm).append(" ").append(paramElement.getSimpleName()).append(",");
                        paramsSb.append(".put(").append('\n').append(paramElement.getSimpleName()).append('\n').append(",")
                                .append(paramElement.getSimpleName()).append(")").append('\n');
                    }
                    if(type.getParameterTypes().size()>0)
                        sb.deleteCharAt(sb.length());
                } catch (NoSuchFieldException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                }
                sb.append(")").append("{").append("\n");
                sb.append("Request request=new Request(\"http:www.baidu.com\")\n")
                        .append(paramsSb);
                sb.append(".setListener(new SimpleListener<").append(type.getReturnType()).append('>').append("(){").append('\n')
                        .append("@Override").append('\n')
                        .append("public void onResponseListener(Request r,").append(type.getReturnType()).append(' ').append("result){").append('\n')
                        .append("}\n")
                        .append("});\n")
                        .append("return null;").append('\n').append("}").append('\n');

            }
            try {
                Writer writer=processingEnv.getFiler().createSourceFile("com.fastlib.Test",classElement.iterator().next()).openWriter();
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
