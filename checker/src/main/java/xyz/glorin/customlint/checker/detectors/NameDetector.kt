package xyz.glorin.customlint.checker.detectors

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.visitor.AbstractUastVisitor
import java.util.*

class NameDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return listOf<Class<out UElement?>>(UClass::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {
            override fun visitClass(node: UClass) {
                node.accept(NamingConventionVisitor(context, node))
            }
        }
    }

    companion object {
        val ISSUE = Issue.create(
            "Naming was wrong!",
            "Please use Camel-Case! start with small letter!",
            "Use Camel-Case!",
            Category.USABILITY,
            5,
            Severity.WARNING,
            Implementation(NameDetector::class.java, EnumSet.of(Scope.JAVA_FILE))
        )
    }

    inner class NamingConventionVisitor : AbstractUastVisitor {
        lateinit var javaContext: JavaContext
        var uClass: UClass

        constructor(javaContext: JavaContext, uClass: UClass) : super() {
            this.javaContext = javaContext
            this.uClass = uClass
        }

        override fun visitClass(node: UClass): Boolean {
            return getVisitClassState(node)
        }

        override fun visitMethod(node: UMethod): Boolean {
            if (!node.isConstructor) {
                var char = node.name[0]
                val code = char.toInt()
                if (code in 66..91) {
                    javaContext.report(
                        ISSUE,
                        javaContext.getNameLocation(node),
                        "the  name of class must start with lowercase:${node.name}"
                    )
                    return true
                }
            }
            return super.visitMethod(node)
        }

        private val getVisitClassState: (UClass) -> Boolean = {
            var result = false
            var char = it.name?.get(0)
            if (char != null) {
                val code = char.toInt()
                if (code in 98..121) {
                    javaContext.report(
                        ISSUE,
                        javaContext.getNameLocation(it),
                        "the  name of class must start with uppercase:${it.name}"
                    )
                    result = true
                }
            }
            result
        }
    }
}