import org.openrndr.application
import org.openrndr.color.ColorHSVa
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

        mouse.cursorVisible = false

        extend {
            drawer.clear(
                ColorHSVa(mouse.position.y / 2.0, 1.0, 1.0,).toRGBa()
            )

            drawer.fill = ColorHSVa(360.0 - mouse.position.y / 2.0, 1.0, 1.0).toRGBa()
            drawer.stroke = null

            val size = mouse.position.x + 1.0
            val position = 360.0 - size / 2.0
            drawer.rectangle(position, position, size, size)
        }
    }
}