package xyz.glorin.customlint.checker.detectors

import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.Category.Companion.LINT
import com.android.tools.lint.detector.api.Detector.UastScanner
import com.android.tools.lint.detector.api.Scope.Companion.JAVA_FILE_SCOPE
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

class NewThreadDetector : Detector(), UastScanner {
    override fun getApplicableConstructorTypes(): List<String>? {
        return listOf("java.lang.Thread")
    }

    override fun visitConstructor(
        context: JavaContext,
        node: UCallExpression,
        constructor: PsiMethod
    ) {
        context.report(
            ISSUE, node, context.getLocation(node),
            "避免自己创建Thread"
        )
    }

    companion object {
        val ISSUE: Issue = Issue.create(
            "NewThread",
            "避免自己创建Thread",
            "请勿直接调用new Thread()，建议使用统一的线程管理工具类",
            LINT,
            5, Severity.ERROR,
            Implementation(NewThreadDetector::class.java, JAVA_FILE_SCOPE)
        )
    }
}