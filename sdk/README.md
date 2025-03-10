# 该项目使用 Kotlin/Js 构建

1. 该项目使用 Kotlin/Js 构建，它可以将 Kotlin 代码编译成 JavaScript 代码，然后运行在 AutoJS中运行。  
2. 想要使用本项目需要 Node 环境,通常构建脚本会自动下载.
   * 如果下载失败，请在 [gradle.properties](gradle.properties) 设置代理。
   * 如果使用本地 Node 将 [build.gradle.kts](build.gradle.kts) 中的 `rootProject.the<NodeJsRootExtension>().download` 设置为 false
3. 使用**compile** gradle 命令来完成项目构建,生成文件位于 `build/autojs/intermediate_compilation_files`
   * 如果[gradle.properties](gradle.properties)中设置`kotlin.js.ir.output.granularity=whole-program`那么生产的编译文件可以在 autojs 中直接运行
4. 使用**webpack** gradle 命令来完成项目打包,生成文件位于 `build/autojs/compilation`
   * 会调用 webpack 打包脚本,将`compile`生成的文件打包到 `build/autojs/compilation`
   * 与 webpack 相关的配置项目([gradle.properties](gradle.properties)):
     * autojs.webpack.intermediate.files=false # 是否打包时打包中间编译文件，而不是最初的编译文件。适用于将中间文件修改后重新打包
     * autojs.webpack.pre.run.compose=false # 是否在打包前运行 compose 任务，默认为 true
     * .....
5. 项目以预先为你引入了更多的依赖如果不需要该依赖可以到 [build.gradle.kts](build.gradle.kts) 中删除

# 配置文件
[config.properties](config%2Fconfig.properties) 文件与Gradle 脚本相关.    
[config/webpack.config.js](config%2Fwebpack.config.js) 与 Node Webpack脚本相关.

# 加载脚本
1. 使用 loadJsFile 函数可以执行脚本
   * loadJsFile("test.js") 如果文件最后返回的是一个 function 的话, 可以使用 loadJsFile("test.js").call() 调用
2. 使用 require 方法可以加载脚本文件.
   * 通过 eval 或者 js 等函数方式调用. 
   * 通过原生方式调用, 需要使用 external 等关键字声明一些 kotlin 方法或者其他

# resources 目录
该目录下打的所有内容都会一并打包并保持目录结构,并且与 main.js 处于一个文件夹下.

# 快速修改编译脚本
1. 在 [intermediate_compilation_files](build%2Fautojs%2Fintermediate_compilation_files) 下 你的项目名称.js 文件就是编译后的文件. 修改它后使用 webpack 命令将重新打包.

# new 一个对象
1. 通过 kotlin 默认的方式创建对象 Date()
2. 使用 new 方法创建一个对象. `JSObject.New(formData_js)` 如: lib.module.FormDataFactory.create
   * 该方法适用于第一种方法无法创建对象的情况下
3. 使用 js 方法创建对象,并使用 kotlin 接收 `js("new formData_js()")`

# 导出方法
使用以下注解可以导出内容，否则没有使用到的内容会被编译器优化掉
```kotlin
@OptIn(ExperimentalJsExport::class)
@JsExport
```

# 通过 Html 编写界面UI
参见 [Autojs与浏览器交互.kt](src%2FjsMain%2Fkotlin%2F%D1%F9%C0%FD%2FAutojs%D3%EB%E4%AF%C0%C0%C6%F7%BD%BB%BB%A5.kt) 和 [web](src%2FjsMain%2Fresources%2Fweb)

# SDK 源码参见
https://github.com/zimoyin/autojs_kotlin_sdk


# 工具
## 映射工具
映射编译后的行号回原本的kotlin 行号中  
连接: https://github.com/zimoyin/autojsx_kotlin_js_map_tool  
注意：该工具使用 nodejs 进行运行，需要 nodejs 环境

