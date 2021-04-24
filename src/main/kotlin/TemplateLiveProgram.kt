import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.olive.oliveProgram
import utils.QuitOnEsc

fun main() = application {
    configure {
        width = 720
        height = 720
    }
    oliveProgram {
        extend(QuitOnEsc())
        extend(Screenshots())

        extend {

        }
    }
}