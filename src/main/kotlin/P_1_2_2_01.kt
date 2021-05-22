import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.extensions.Screenshots
import org.openrndr.math.Vector2
import utils.QuitOnEsc
import java.lang.Double.max
import java.nio.ByteBuffer

interface PixelInterface {
    fun makeReady()
    operator fun get(x: Int, y: Int): ColorRGBa
}

fun main() = application {
    configure {
        width = 800
        height = 800
    }

    program {
        extend(QuitOnEsc())
        extend(Screenshots())


        fun sizedPixelData(image: ColorBuffer): PixelInterface {
            val rtImage = renderTarget(width, height) { colorBuffer(
                format = ColorFormat.RGB,
                type = ColorType.FLOAT32
            ) }

            drawer.withTarget(rtImage) {
                drawer.image(image, Vector2.ZERO, drawer.bounds.width, drawer.bounds.height)
            }

            val shadow = rtImage.colorBuffer(0).shadow
            shadow.download()

            return object : PixelInterface {
                override fun makeReady() {}
                override operator fun get(x: Int, y: Int) = shadow[x, y]
            }
        }

//        var colorComparator: Comparator<ColorRGBa> = Comparator { a, b ->
//            a.luminance.compareTo(b.luminance)
//        }

        // Potentially higher performance but would never figure out…
//        fun sizedPixelData(image: ColorBuffer): PixelInterface {
//            val rtImage = renderTarget(width, height) { colorBuffer(
//                format = ColorFormat.RGB,
//                type = ColorType.FLOAT32
//            ) }
//
//            drawer.withTarget(rtImage) {
//                drawer.image(image, Vector2.ZERO, drawer.bounds.width, drawer.bounds.height)
//            }
//
//            val cb = rtImage.colorBuffer(0)
//            val bfMultiplier = cb.format.componentCount * cb.type.componentSize
//            val capacity = cb.width * cb.height * bfMultiplier
//            val bytes = ByteBuffer.allocateDirect(capacity)
//            cb.read(bytes)
//            val doubles = bytes.asDoubleBuffer()
//
//            return object : PixelInterface {
//                override fun makeReady() {
//                    bytes.rewind()
//                }
//
//                override fun get(x: Int, y: Int): ColorRGBa {
//                    val i = x * y
//                    val r = doubles[i]
//                    val g = doubles[i+1]
//                    val b = doubles[i+3]
//                    return ColorRGBa(r,g,b)
//                }
//            }
//        }

        // Used to access pixel data
        var image = loadImage("data/images/cheeta.jpg")
        var imageData = sizedPixelData(image)

        // TODO: use buffer instead of shadow for increased performance?

        window.drop.listen {
            it.files.firstOrNull()?.let { file ->
                image = loadImage(file)
                imageData = sizedPixelData(image)
            }
        }

        extend {
            val colors = mutableListOf<ColorRGBa>()
            val rectSize = max(mouse.position.x, 10.0)
            val rectSizeI = rectSize.toInt()

            imageData.makeReady()
            for (y in 0 until height step rectSizeI) {
                for (x in 0 until width step rectSizeI) {
                    colors.add(imageData[x, y])
                }
            }

//            colors.sortWith { a, b ->
//                a.luminance.compareTo(b.luminance)
//            }
            // TODO: sort the colors

            drawer.stroke = null

            var x = 0.0
            var y = 0.0
            colors.forEach {
                x += rectSize
                if (x > width) {
                    x = 0.0
                    y += rectSize
                }

                drawer.fill = it
                drawer.rectangle(x, y, rectSize, rectSize)
            }
        }
    }
}
