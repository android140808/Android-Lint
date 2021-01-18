package xyz.glorin.customlint.checker.detectors

import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.Category.Companion.LINT
import com.android.tools.lint.detector.api.Detector.UastScanner
import com.android.tools.lint.detector.api.Scope.Companion.JAVA_FILE_SCOPE
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.*
import java.util.*

/**
 * 检测循环
 *
 */
class ConcurrentModifyDetector : Detector(), UastScanner {
    override fun getApplicableMethodNames(): List<String>? {
        return listOf("remove")
    }

    override fun visitMethodCall(
        context: JavaContext,
        node: UCallExpression,
        method: PsiMethod
    ) {
        if (!context.evaluator.isMemberInClass(method, "java.util.List")) {
            return
        }
        val expression = node.receiver as? USimpleNameReferenceExpression ?: return
        val listName = expression.identifier
        var parent: UElement? = node
        var tmpElemnt: UElement? = null
        var findBlock = false
        while (true) {
            parent = parent!!.uastParent
            if (parent == null || parent is UMethod
                || parent is UClass
                || parent is UFile
            ) {
                return
            }
            if (!findBlock && parent is UBlockExpression) {
                findBlock = true
                if (findBreakOrReturn(parent, tmpElemnt)) {
                    return
                }
            }
            if (!findBlock) {
                tmpElemnt = parent
            }
            if (findBlock && findForEachExpression(parent, listName)) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "可能引起ConcurrentModificationException"
                )
                return
            }
        }
    }

    companion object {
        val ISSUE = Issue.create(
            "ConcurrentModificationException",
            "可能引起ConcurrentModificationException",
            "请确保代码不会引起ConcurrentModificationException",
            LINT,
            9, Severity.ERROR,
            Implementation(ConcurrentModifyDetector::class.java, JAVA_FILE_SCOPE)
        )

        /**
         * 若有break 或者return 语句，则排除。
         */
        private fun findBreakOrReturn(parent: UElement, tmpElemnt: UElement?): Boolean {
            val expressions = (parent as UBlockExpression).expressions
            val index = if (tmpElemnt is UExpression) expressions.indexOf(tmpElemnt) else 0
            for (i in expressions.size - 1 downTo index) {
                val exp = expressions[i]
                if (exp is UBreakExpression
                    || exp is UReturnExpression
                ) {
                    return true
                }
            }
            return false
        }

        private fun findForEachExpression(parent: UElement, listName: String): Boolean {
            if (parent !is UForEachExpression) {
                return false
            }
            val forListName = (parent.iteratedValue as USimpleNameReferenceExpression).identifier
            return forListName == listName
        }
    }
}