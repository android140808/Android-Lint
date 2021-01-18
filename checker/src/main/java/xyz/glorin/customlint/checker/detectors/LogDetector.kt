package xyz.glorin.customlint.checker.detectors

import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.Category.Companion.LINT
import com.android.tools.lint.detector.api.Detector.UastScanner
import com.android.tools.lint.detector.api.Scope.Companion.JAVA_FILE_SCOPE
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import java.util.*

class LogDetector : Detector(), UastScanner {
    override fun getApplicableMethodNames(): List<String>? {
        return Arrays.asList("v", "d", "i", "w", "e", "wtf")
    }

    override fun visitMethodCall(
        context: JavaContext,
        node: UCallExpression,
        method: PsiMethod
    ) {
        if (context.evaluator.isMemberInClass(method, "android.util.Log")) {
            context.report(ISSUE, node, context.getLocation(node), "避免调用android.util.Log")
        }
    }

    companion object {
        val ISSUE: Issue = Issue.create(
            "LogUsage",
            "避免调用android.util.Log",
            "请勿直接调用android.util.Log，应该使用统一工具类",
            LINT, 5, Severity.WARNING,
            Implementation(LogDetector::class.java, JAVA_FILE_SCOPE)
        )
    }
}