package hanlonglin.com.butterknifesimple;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ButterKnife {

    public static boolean bind(Activity target){
        return bind(target,target.getWindow().getDecorView());
    }

    private static boolean bind(Object target,View view){
        Class<?> clazz=target.getClass();
        Constructor<?> bindingConstructorForClass = findBindingConstructorForClass(clazz);
        if(null!=bindingConstructorForClass)
        {
            try {
                bindingConstructorForClass.newInstance(target,view);
                return true;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static Constructor<?> findBindingConstructorForClass(Class<?> clazz) {
        String clsName=clazz.getName();
        try {
            Class<?> bindClass=clazz.getClassLoader().loadClass(clsName+"_ViewBinding");
            Constructor<?> constructor = bindClass.getConstructor(clazz, View.class);
            return  constructor;
        } catch (ClassNotFoundException e) {
           //如果没有找到从父类找
            return findBindingConstructorForClass(clazz.getSuperclass());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
