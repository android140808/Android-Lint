package xyz.glorin.customlint.checker

import com.android.tools.lint.client.api.IssueRegistry
import xyz.glorin.customlint.checker.detectors.*

class CustomRegistry : IssueRegistry() {
    override val issues = listOf(
        LogDetector.ISSUE,
        NameDetector.ISSUE,
        ConcurrentModifyDetector.ISSUE,
        DrawableAttrDetector.ISSUE,
        LinearLayoutManagerDetector.ISSUE,
        NewThreadDetector.ISSUE,
        PopupWindowDetector.ISSUE,
        PrintStackTraceDetector.ISSUE,
        ReflectDetector.ISSUE
    )
}