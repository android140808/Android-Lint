package xyz.glorin.customlint.checker.detectors

import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.Category.Companion.LINT
import com.android.tools.lint.detector.api.Detector.UastScanner
import com.android.tools.lint.detector.api.Scope.Companion.JAVA_FILE_SCOPE
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import java.util.*

/**
 * @author lishouxian on 2019/4/17.
 */
class LinearLayoutManagerDetector : Detector(), UastScanner {
    override fun getApplicableConstructorTypes(): List<String>? {
        return Arrays.asList(
            "androidx.recyclerview.widget.LinearLayoutManager",
            "android.support.v7.widget.LinearLayoutManager"
        )
    }

    override fun visitConstructor(
        context: JavaContext,
        node: UCallExpression,
        constructor: PsiMethod
    ) {
        val uParent = node.uastParent ?: return
        val psiElement = uParent.javaPsi ?: return
        var findKeyMethod = false
        var nextSibling = psiElement.parent
        //往下找最多5行（每一行由PsiWhiteSpace + PsiDeclarationStateMent组成）
        var count = 10
        while (count > 0 && nextSibling != null) {
            nextSibling = nextSibling.nextSibling
            count--
            if (nextSibling == null) {
                break
            }

            //跳过注释
            if (nextSibling is PsiComment) {
                continue
            }
            var text = nextSibling.text ?: continue
            text = text.trim { it <= ' ' }
            if (text.isEmpty()) {
                continue
            }
            if (text.contains("setRecycleChildrenOnDetach")) {
                findKeyMethod = true
                break
            }
        }
        if (!findKeyMethod) {
            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "如需覆写Adapter的onViewDetachedFromWindow方法，请调用LinearLayoutManager#setRecycleChildrenOnDetach方法"
            )
        }
    }

    companion object {
        val ISSUE: Issue = Issue.create(
            "LinearLayoutManagerDetector",
            "如需覆写Adapter的onViewDetachedFromWindow方法，请调用LinearLayoutManager#setRecycleChildrenOnDetach方法",
            "默认情况下，Adapter的onViewDetachedFromWindow在页面退出时，不会被调用。如果有解注册的行为，可能会引起内存泄漏",
            LINT,
            5, Severity.ERROR,
            Implementation(LinearLayoutManagerDetector::class.java, JAVA_FILE_SCOPE)
        )
    }
}