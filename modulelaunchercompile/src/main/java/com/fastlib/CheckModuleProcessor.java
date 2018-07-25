package com.fastlib;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Created by Administrator on 2018/7/24.
 */
@SupportedAnnotationTypes("com.fastlib.Module")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class CheckModuleProcessor extends AbstractProcessor{
    private static final String CLASS_NAME="Fastlib$Function$ModuleWarehouse";
    private Messager mMessager;
    private Elements mElementUtil;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessager=processingEnvironment.getMessager();
        mElementUtil=processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment){
        Set<? extends Element> classElements=roundEnvironment.getElementsAnnotatedWith(Module.class);

        mMessager.printMessage(Diagnostic.Kind.WARNING,"module processor start");
        if(classElements.size()>0){
            final String PACKAGE_NAME=mElementUtil.getPackageOf(classElements.iterator().next()).getQualifiedName().toString();
            final String PACKAGE_NAME_DEFINE="package "+PACKAGE_NAME+";";
            StringBuffer mapStrBuffer=new StringBuffer();

            mapStrBuffer.append("{{");
            for(Element e:classElements){
                mMessager.printMessage(Diagnostic.Kind.NOTE,"processing "+e.toString());
                Module module=e.getAnnotation(Module.class);

                if(module.value().isEmpty()){
                    throw new IllegalArgumentException("module path can't be empty");
                }
                //put("module name$group","class name");
                mapStrBuffer.append("put(").append('"').append(module.value()).append('$').append(module.group()).append('"').append(",")
                        .append('"').append(e.toString()).append('"').append("); ");
            }
            mapStrBuffer.append("}}");
            StringBuilder classString=new StringBuilder();

            classString.append(PACKAGE_NAME_DEFINE).append("\n")
                    .append("import java.util.Map;").append("\n")
                    .append("import java.util.HashMap;").append("\n\n")
                    .append("public final class ").append(CLASS_NAME).append("{").append("\n")
                    .append("\t").append("private Map<String,String> mPathMap=new HashMap<String,String>()").append("\n")
                    .append("\t").append(mapStrBuffer).append(";     //path-->module class name").append("\n")
                    .append("}");
            try {
                Writer writer=processingEnv.getFiler().createSourceFile(PACKAGE_NAME+"."+CLASS_NAME,classElements.iterator().next()).openWriter();
                writer.append(classString.toString());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
