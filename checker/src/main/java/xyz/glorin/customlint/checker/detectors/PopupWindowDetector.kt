package xyz.glorin.customlint.checker.detectors

import com.android.tools.lint.detector.api.*
import com.android.tools.lint.detector.api.Category.Companion.LINT
import com.android.tools.lint.detector.api.Detector.UastScanner
import com.android.tools.lint.detector.api.Scope.Companion.JAVA_FILE_SCOPE
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UastFacade

/**
 * @author lishouxian on 2019/4/17.
 */
class PopupWindowDetector : Detector(), UastScanner {
    override fun getApplicableConstructorTypes(): List<String>? {
        return listOf("android.widget.PopupWindow")
    }

    override fun visitConstructor(
        context: JavaContext,
        node: UCallExpression,
        constructor: PsiMethod
    ) {
        val uParent = node.uastParent ?: return
        val psiElement = uParent.javaPsi ?: return
        var reportElement: UElement? = node
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
            if (text.contains("setBackgroundDrawable(null)")) {
                reportElement = UastFacade.convertElement(nextSibling, null, null)
                break
            }
            if (text.contains("setBackgroundDrawable")) {
                findKeyMethod = true
                break
            }
        }
        if (reportElement == null) {
            reportElement = node
        }
        if (!findKeyMethod) {
            context.report(
                ISSUE,
                reportElement,
                context.getLocation(reportElement),
                "如需在区域外点击消失，需要设置背景不为null，否则6.0以下不起作用"
            )
        }
    }

    companion object {
        val ISSUE: Issue = Issue.create(
            "PopupWindowDetector",
            "如需在区域外点击消失，需要设置背景不为null，否则6.0以下不起作用",
            "",
            LINT,
            5, Severity.ERROR,
            Implementation(PopupWindowDetector::class.java, JAVA_FILE_SCOPE)
        )
    }
}