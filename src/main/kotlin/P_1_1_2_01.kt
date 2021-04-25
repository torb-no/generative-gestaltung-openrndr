import org.openrndr.application
import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.math.Vector2
import org.openrndr.shape.contour
import utils.QuitOnEsc
import utils.d
import utils.toRadians
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

fun main() = application {
    configure {
        width = 800
        height = 800
    }

    oliveProgram {
        extend(QuitOnEsc())
        extend(Screenshots())

        val radius = min(width, height) / 2

        var segmentCount = 360
        keyboard.keyDown.listen {
            segmentCount = when(it.name) {
                "1" -> 360
                "2" -> 45
                "3" -> 24
                "4" -> 12
                "5" -> 6
                else -> segmentCount
            }
        }

        extend {
            drawer.clear(ColorRGBa.WHITE)
            drawer.stroke = null

            val angleStep = 360 / segmentCount

            var prevPoint = drawer.bounds.center + Vector2(
                cos(0.0) * radius,
                sin(0.0) * radius
            )

            for (angle in 0..360 step angleStep) {
                val angleRadians = angle.toRadians()

                val newPoint = drawer.bounds.center + Vector2(
                    cos(angleRadians) * radius,
                    sin(angleRadians) * radius
                )

                val c = contour {
                    moveTo(drawer.bounds.center)
                    lineTo(prevPoint)
                    lineTo(newPoint)
                    close()
                }

                val saturation = mouse.position.x / width
                val lightness = mouse.position.y / height
                drawer.fill = ColorHSVa(angle.d, saturation, lightness).toRGBa()

                drawer.contour(c)

                prevPoint = newPoint
            }

        }
    }
}