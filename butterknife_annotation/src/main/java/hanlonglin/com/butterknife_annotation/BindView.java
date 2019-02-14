package hanlonglin.com.butterknife_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//目标 （标注在属性上）
@Target(ElementType.FIELD)
//保留到 需要反射的时候使用runtime 其他情况使用class
@Retention(RetentionPolicy.CLASS)
public @interface BindView {
    int value();
}
