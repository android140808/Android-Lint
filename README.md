Issues vs Detectors
首先来区分一下这两个概念。Issue 代表您想要发现并提示给开发者的一种问题，包含描述、更全面的解释、类型和优先级等等。
官方提供了一个 Issue 类，您只需要实例化一个 Issue，并注册到 IssueRegistry 里。

另外您还需要实现一个 Detector。Detector 负责扫描代码并找到有问题的地方，然后把它们报告出来。
一个 Detector 可以报告多种类型的 Issue，您可以针对不同类型的问题使用不同的严重程度，
这样用户可以更精确地控制他们想要看到的内容。

Issue
先看 Issue.create() 方法，其参数定义如下:
    id: 唯一的 id，简要表达当前问题。
    briefDescription: 简单描述当前问题。
    explanation: 详细解释当前问题和修复建议。
    category: 问题类别，在 Android 中主要有如下六大类:
        SECURITY: 安全性。例如在 AndroidManifest.xml 中没有配置相关权限等。
        USABILITY: 易用性。例如重复图标，一些黄色警告等。
        PERFORMANCE: 性能。例如内存泄漏，xml 结构冗余等。
        CORRECTNESS: 正确性。例如超版本调用 API，设置不正确的属性值等。
        A11Y: 无障碍 (Accessibility)。例如单词拼写错误等。
        I18N: 国际化 (Internationalization)。例如字符串缺少翻译等。
    priority: 优先级，从 1 到 10，10 最重要。
    severity: 严重程度，包括 FATAL、ERROR、WARNING、INFORMATIONAL 和 IGNORE。
    implementation: Issue 和哪个 Detector 绑定，以及声明检查的范围。
    
Scopes
再来说说上面创建的 Implementation 对象，它的构造方法的第二个参数传入一个 Scope 枚举类的集合，包括:
    资源文件
    Java 源文件
    Class 文件
    Proguard 配置文件
    Manifest 文件
    等等
    
Scanner
自定义 Detector 还需要实现一个或多个以下接口:
    UastScanner: 扫描 Java 文件和 Kotlin 文件
    ClassScanner: 扫描 Class 文件
    XmlScanner: 扫描 XML 文件
    ResourceFolderScanner: 扫描资源文件夹
    BinaryResourceScanner: 扫描二进制资源文件
    OtherFileScanner: 扫描其他文件
    GradleScanner: 扫描 Gradle 脚本
    
eg:翻看 lint-checks 查看源码的 Detector,
    重写对应的 Detector 来适应自己的规则即可
    

