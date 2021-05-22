import org.openrndr.application
import org.openrndr.color.ColorHSLa
import org.openrndr.color.ColorRGBa
import org.openrndr.color.ConvertibleToColorRGBa
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.noise.random
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extras.color.palettes.colorSequence
import org.openrndr.extras.color.spaces.toHSLUVa
import org.openrndr.math.Vector2
import org.openrndr.math.map
import spaces.toOKLABa
import spaces.toOKLCHa
import utils.QuitOnEsc
import java.lang.NumberFormatException

// TODO: Add swatch export

private const val MAX_COLOR_LINES = 10

typealias ColorConverter = (ColorRGBa) -> ConvertibleToColorRGBa

private val converters = listOf<Pair<String, ColorConverter>>(
    "(no converter)" to { it }, // 0
    "RGB" to ColorRGBa::toRGBa,
    "OKLABa"  to ColorRGBa::toOKLABa,
    "LinearRGB" to ColorRGBa::toLinear,
    "LAB" to { it.toLABa() },
    "OKLCh" to ColorRGBa::toOKLCHa,
    "HSLUV" to ColorRGBa::toHSLUVa,
    "LUV" to { it.toLUVa() },
    "HSVa" to ColorRGBa::toHSVa,
    "HSLa" to ColorRGBa::toHSLa,
)

fun main() = application {
    configure {
        width = 800
        height = 800
    }

    program {
        extend(QuitOnEsc())
        extend(Screenshots())

        var convert: ColorConverter = ColorRGBa::toRGBa

        keyboard.keyDown.listen {
            val index = try {
                val number = it.name.toInt()
                number
            } catch (e: NumberFormatException) {
                // Ignore non-number inputs
                return@listen
            }

            val (name, converter) = try {
                converters[index]
            } catch (e: ArrayIndexOutOfBoundsException) {
                println("No converter assigned to $index")
                return@listen
            }

            println(name)
            convert = converter
        }

        // Using RGBa as result here as it easy to
        // get into the other colours
        @Suppress("UNUSED_PARAMETER")
        fun colorEndsGenerator(i: Int) =
            ColorHSLa(
                random(0.0, 60.0),
                random(0.25, 1.0),
                random(0.33, 1.0),
            ).toRGBa() to
            ColorHSLa(
                random(160.0, 190.0),
                0.4,
                random(0.0, 0.4),
            ).toRGBa()

        var tileCountY = MAX_COLOR_LINES

        // First of pair is the left, second of pair is the right
        // These are the colors we interpolate between
        val colorLineEnds = MutableList(MAX_COLOR_LINES, ::colorEndsGenerator)

        mouse.buttonUp.listen {
            // Shake/randomize colors
            for (i in 0 until tileCountY) {
                colorLineEnds[i] = colorEndsGenerator(i)
            }
        }

        extend {
            drawer.stroke = null

            tileCountY = mouse.position.y.map(
                0.0, drawer.bounds.height,
                2.0, 10.0,
                true).toInt()
            val tileCountX = mouse.position.x.map(
                0.0, drawer.bounds.width,
                2.0, 100.0,
                true).toInt()

            val tileWidth = drawer.bounds.width / tileCountX
            val tileHeight = drawer.bounds.height / tileCountY

            for (gridY in 0 until tileCountY) {
                val colorEnds = colorLineEnds[gridY]

                // Convert to selected color space
                val colLeft = convert(colorEnds.first)
                val colRight = convert(colorEnds.second)

                // Create the blend we use
                val colSeq = colorSequence(0.0 to colLeft, 1.0 to colRight)
                val colBlend = colSeq blend tileCountX

                for (gridX in 0 until tileCountX) {
                    drawer.fill = colBlend[gridX]
                    val position = Vector2(
                        gridX * tileWidth,
                        gridY * tileHeight
                    )
                    drawer.rectangle(position, tileWidth, tileHeight)
                }

            }
        }
    }
}