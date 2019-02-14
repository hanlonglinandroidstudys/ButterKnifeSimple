1.注解分为两种：
    1).运行时注解，因为性能问题被一些人所诟病，通过反射
    2).编译器注解 编译时通过注解标记进行额外处理

2.APT
Annotation Processing Tool 是一种处理注释的工具，它对源码进行检测找出其中的Annotation,根据注解自动生成代码

使用APT的步骤：
    1.创建java模块定义注解
    注解处理器基于注解，一次需要创建一个模块编写自定义注解。同时此模块需要分别被注解处理器模块（Java）与使用注解处理器模块（Android）依赖,
    所以必须时java模块。

    2.创建java模块实现注解处理器
    APT是javac编译器提供的一项参数，实现需要基于jdk中的AbstractProcessor类实现。这个模块不会被打包进apk，而是在编译时期由编译器执行。

    3.使用注解处理器
    annotationProcessor project(":butterknife_compiler")
    annotationProcessor是Android的Gradle插件提供的APT方式


项目结构：

--butterknife_annotation java模块 注解定义模块
    *声明注解的属性，使用范围等

--butterknife_compiler java模块 注解实现模块
    *继承AbstractProcessor，实现注解；
    *实现步骤：
        1.创建类ButterKnifeProcessor 继承 AbstactProcessor ,并使用com.google.auto.service注册注解处理器
        2.重写ButterKnifeProcessor中的process方法
        3.采集信息，包括所有使用了我们定义的注解的节点，所属类名
        4.借助javapoet框架创建采集到的所有类，在类的后面拼接一个 _ViewBinding
        5.每个类里面创建构造方法，传入Activity和View对象
        6.在构造方法里添加findViewById语句，如下：
        methodBuilder.addStatement("target.$N=source.findViewById($L)",
                                    simpleName,annotation.value());
        7.将创建的类生成到和原来的类相同的包名中（这样属性不是public也能用）

--app  Android模块，注解使用模块
    1.定义ButterKnife类，这个需要是一个Android类，因为要使用Activity和View类；
        ButterKnife编写bind方法，通过传入的Activity对象，通过反射找到对应的_ViewBinding类，然后调用它的构造方法，
        而在构造函数中之前已经定义了findViewById放法，所以这样就完成了初始化组件
    2.使用注解：
        1.在组件前面添加BindView注解
        2.在setContentView语句之后调用ButterKnife.bind(this)
        3.之后就可以调用组件的其他方法了
