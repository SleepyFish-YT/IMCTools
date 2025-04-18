package me.sleepyfish.imctools.notification

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project

object MyPluginNotifier {

    private const val NOTIFICATION_GROUP = "imc.tools.notifications"

    fun showWarning(project: Project?, message: String) {
        val notification = Notification (
            NOTIFICATION_GROUP,
            "IMCTools - by SleepyFish :3",
            message,
            NotificationType.WARNING
        )

        Notifications.Bus.notify(notification, project)
        notification.notify(project)
    }

    fun showInfo(project: Project?, message: String) {
        val notification = Notification (
            NOTIFICATION_GROUP,
            "IMCTools - by SleepyFish :3",
            message,
            NotificationType.INFORMATION
        )

        Notifications.Bus.notify(notification, project)
    }
}