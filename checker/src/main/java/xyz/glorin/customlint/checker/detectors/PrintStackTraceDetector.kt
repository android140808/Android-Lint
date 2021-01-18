package xyz.glorin.customlint.checker.detectors

import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.Category.Companion.LINT
import com.android.tools.lint.detector.api.Detector.UastScanner
import com.android.tools.lint.detector.api.Scope.Companion.JAVA_FILE_SCOPE
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import java.util.*

class PrintStackTraceDetector : Detector(), UastScanner {
    override fun getApplicableMethodNames(): List<String>? {
        return Arrays.asList("printStackTrace")
    }

    override fun visitMethodCall(
        context: JavaContext,
        node: UCallExpression,
        method: PsiMethod
    ) {
        if (context.evaluator.isMemberInClass(method, "java.lang.Throwable")) {
            /*
            报告该问题
             */
            context.report(
                ISSUE,
                method,
                context.getLocation(node),
                "直接调用Throwable.printStackTrace()可能引起OOM，使用自定义方法替代",
                getLintFix(context, node)
            )
        }
    }

    /**
     * lint自动修复
     */
    private fun getLintFix(
        context: JavaContext,
        node: UCallExpression
    ): LintFix {
        /*
            先检查当前文件是否import了我们需要的类
             */
        var hasImport = false
        val list = context.uastFile!!.imports
        for (statement in list) {
            val element = statement.importReference
            if ("com.sollian.customlintrules.utils.LogUtils".endsWith(element!!.asRenderString())) {
                hasImport = true
                break
            }
        }

        /*
        第一个修复，替换方法调用
        */
        val fix = fix().replace()
            .all()
            .with("LogUtils.printStackTrace(" + node.receiver!!.asRenderString() + ')')
            .autoFix()
            .build()

        /*
         第二个修复，import LogUtils类
         */
        var importFix: LintFix? = null
        if (!hasImport) {
            val statement = list[list.size - 1]
            val lastImport = statement.asRenderString() + ';'
            importFix = fix().replace() //最后的一条import语句
                .text(lastImport) //替换为最后一条import语句，加上LogUtils类
                .with("$lastImport\nimport com.sollian.customlintrules.utils.LogUtils;") //替换位置
                .range(context.getLocation(statement))
                .autoFix()
                .build()
        }

        /*
         最终的修复方案
         */
        val builder = fix().name("使用LogUtils.printStackTrace替换").composite()
        builder.add(fix)
        if (importFix != null) {
            builder.add(importFix)
        }
        return builder.build()
    }

    companion object {
        val ISSUE: Issue = Issue.create(
            "PrintStackTraceUsage",
            "避免直接调用Throwable.printStackTrace()",
            "直接调用Throwable.printStackTrace()可能引起OOM",
            LINT, 5, Severity.ERROR,
            Implementation(PrintStackTraceDetector::class.java, JAVA_FILE_SCOPE)
        )
    }
}