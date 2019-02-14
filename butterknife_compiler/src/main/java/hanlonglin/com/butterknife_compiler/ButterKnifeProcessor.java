package hanlonglin.com.butterknife_compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import hanlonglin.com.butterknife_annotation.BindView;

//注册 相当于在manifest中注册activity一样
@AutoService(Processor.class)

//只需要处理BindView
@SupportedAnnotationTypes("hanlonglin.com.butterknife_annotation.BindView")

public class ButterKnifeProcessor extends AbstractProcessor {

    private Messager messager;
    private Elements elementUtils;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
    }

    /**
     * @param set              //所有使用了注册的注解的集合
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set,
                           RoundEnvironment roundEnvironment) {

        if (!set.isEmpty()) {
            //需要处理
            //1.采集信息
            //获得被BindView声明的节点
            Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(BindView.class);
            //采集信息
            Map<String, List<Element>> bindViews = new HashMap<>();
            for (Element element : elementsAnnotatedWith) {
                log("节点：" + element.getSimpleName());
                //获取当前节点 (textView)的父节点  类节点 mainActivity
                TypeElement classElement = (TypeElement) element.getEnclosingElement();
                log("类节点：" + classElement.getSimpleName());

                //得到类的类名 全限定名
                String qualifiedName = classElement.getQualifiedName().toString();
                log("全限定名：" + qualifiedName);

                List<Element> elemnets = bindViews.get(qualifiedName);
                if (null == elemnets) {
                    elemnets = new ArrayList<>();
                }
                elemnets.add(element);
                bindViews.put(qualifiedName, elemnets);
            }
            //2.生成一个java类
            //借助javapoet 框架生成java类

            for (Map.Entry<String, List<Element>> entry : bindViews.entrySet()) {
                //类名 hanlonglin.com.butterknifesimple.MainActivity
                String key = entry.getKey();
                //tv1 tv2
                List<Element> values=entry.getValue();

                //获取对应字符串的类节点
                TypeElement typeElement = elementUtils.getTypeElement(key);
                ClassName clsName=ClassName.get(typeElement);

                //获取View的类节点
                ClassName viewName = ClassName.get("android.view", "View");

                //TypeElement typeElement=elementUtils.getTypeElement(key);
                String className = key.substring(key.lastIndexOf(".")+1);
                log("key:"+className);

                //创建一个类
                TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(className + "_ViewBinding")
                        .addModifiers(Modifier.PUBLIC);
                //创建一个方法
                MethodSpec.Builder methodBuilder = MethodSpec
                        .constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(clsName, "target")
                        .addParameter(viewName, "source");

                //[tv,tv1]
                //写代码
                for(Element element:values){
                    Name simpleName = element.getSimpleName();
                    BindView annotation = element.getAnnotation(BindView.class);
                    methodBuilder.addStatement("target.$N=source.findViewById($L)",
                            simpleName,annotation.value());
                }

                TypeSpec typeSpec = typeBuilder.addMethod(methodBuilder.build()).build();
                try {
                    log("packageName:"+clsName.packageName());
                    JavaFile.builder(clsName.packageName(),typeSpec).build().writeTo(filer);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return false;
    }


    private void log(String text) {
        messager.printMessage(Diagnostic.Kind.NOTE, text);
    }
}
