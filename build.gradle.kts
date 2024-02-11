import org.sourcegrade.jagr.gradle.task.grader.GraderRunTask

plugins {
    alias(libs.plugins.algomate)
    alias(libs.plugins.javafxplugin)
}

exercise {
    assignmentId.set("projekt")
}

submission {
    // ACHTUNG!
    // Setzen Sie im folgenden Bereich Ihre TU-ID (NICHT Ihre Matrikelnummer!), Ihren Nachnamen und Ihren Vornamen
    // in Anführungszeichen (z.B. "ab12cdef" für Ihre TU-ID) ein!
    studentId = null
    firstName = null
    lastName = null

    // Optionally require own tests for mainBuildSubmission task. Default is false
    requireTests = false
}

configurations.all {
    resolutionStrategy {
        configurations.all {
            resolutionStrategy {
                force(
                    libs.algoutils.student,
                    libs.algoutils.tutor,
                    libs.junit.pioneer,
                )
            }
        }
    }
}

javafx {
    version = "21"
    modules("javafx.controls", "javafx.fxml", "javafx.swing")
}

jagr {
    graders {
        val graderPublic by getting {
            graderName.set("Projekt-Public")
            rubricProviderName.set("projekt.Projekt_RubricProviderPublic")
        }
    }
}

tasks {
    withType<GraderRunTask> {
        doFirst {
            throw GradleException("Public tests will be released in the next few days.")
        }
    }
}
