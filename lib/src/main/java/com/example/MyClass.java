package com.example;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("com.example.Permission")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class MyClass extends AbstractProcessor{
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment){
        Messager messager=processingEnv.getMessager();

        for(TypeElement te:set){
            messager.printMessage(Diagnostic.Kind.NOTE,te.toString());
            for(Element e:roundEnvironment.getElementsAnnotatedWith(te))
                messager.printMessage(Diagnostic.Kind.NOTE,e.toString());
        }
        try { // write the file
            JavaFileObject source = processingEnv.getFiler().createSourceFile("com.example.annotationprocessor.generated.GeneratedClass");

            Writer writer = source.openWriter();
            writer.write("test");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // Note: calling e.printStackTrace() will print IO errors
            // that occur from the file already existing after its first run, this is normal
        }
        return true;
    }
}
