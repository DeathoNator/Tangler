package com.andrzejn.tangler.screens

import com.andrzejn.tangler.Context
import com.badlogic.gdx.graphics.g2d.Sprite
import kotlin.math.max
import kotlin.math.min

/**
 * Renders board controls and provides reference coordinates and sizes
 */
class Controls(
    /**
     * Reference to the app Context object
     */
    val ctx: Context
) {
    /**
     * Screen tile/cell width
     */
    var tileWidth: Int = 0

    /**
     * Screen tile/cell height
     */
    var tileHeight: Int = 0

    /**
     * X coordinate of the right side of the board
     */
    var boardRightX: Float = 0f

    /**
     * Y coordinate of the top side of the board
     */
    var boardTopY: Float = 0f

    /**
     * X coordinate of the left side of the board
     */
    var boardLeftX: Float = 0f

    /**
     * Y coordinate of the bottom side of the board
     */
    var boardBottomY: Float = 0f

    /**
     * X coordinate of the screen senter
     */
    var centerX: Float = 0f

    /**
     * Y coordinate of the center of the circle at the bottom (where next tile is displayed)
     */
    var circleY: Float = 0f

    private var circleRadius: Float = 0f
    private var rotateButtonY = 0f
    private var rotateLeftX = 0f
    private var rotateRightX = 0f
    private var rotateButtonSize = 0f

    private var lowerButtonY = 0f
    private var leftButtonsX = 0f
    private var rightButtonsX = 0f
    private var lowerButtonSize = 0f
    private var bottomButtonsXOffset = 0f
    private var bottomButtonsYOffset = 0f

    private val sRotateLeft: Sprite = Sprite(ctx.a.rotateleft)
    private val sRotateRight: Sprite = Sprite(ctx.a.rotateright)
    private val sPlay: Sprite = Sprite(ctx.a.play)
    private val sHelp: Sprite = Sprite(ctx.a.help)
    private val sHome: Sprite = Sprite(ctx.a.home)
    private val sExit: Sprite = Sprite(ctx.a.poweroff)
    private val sLogo: Sprite = Sprite(ctx.a.logo)
    private val sDown: Sprite = Sprite(ctx.a.movedown)

    /**
     * Calculate and set all control coordinates, based on the provided board rectangle coordinates
     */
    fun setCoords(leftX: Float, topY: Float, rightX: Float, bottomY: Float, buttonsBaseY: Float) {
        boardLeftX = leftX
        boardTopY = topY
        boardRightX = rightX
        boardBottomY = bottomY
        centerX = ctx.viewportWidth / 2
        val baseWidth = (boardRightX - boardLeftX) / when (ctx.gs.boardSize) {
            6 -> 6
            8 -> 7
            else -> 8
        }
        circleRadius = baseWidth * 0.9f
        circleY = buttonsBaseY - baseWidth - indent * 1.28f
        rotateButtonY = circleY - baseWidth * 0.4f
        rotateLeftX = centerX - baseWidth * 2.1f
        rotateRightX = centerX + baseWidth * 1.05f
        rotateButtonSize = baseWidth * 1f

        lowerButtonSize = baseWidth
        lowerButtonY = rotateButtonY + baseWidth / 2
        leftButtonsX = max(0f, boardLeftX - baseWidth * 2)
        rightButtonsX = min(boardRightX + baseWidth, ctx.viewportWidth - baseWidth)
        bottomButtonsXOffset = 0f
        bottomButtonsYOffset = baseWidth * 1.1f

        if (ctx.gs.boardSize == 10)
            with(sDown) {
                setSize(lowerButtonSize, lowerButtonSize)
                setPosition(centerX - width * 1.8f, circleY - height * 1.5f)
            }
        else
            with(sDown) {
                setSize(lowerButtonSize * 0.9f, lowerButtonSize * 0.9f)
                setPosition(
                    leftButtonsX + lowerButtonSize * 1.1f,
                    lowerButtonY - bottomButtonsYOffset * (if (ctx.gs.sidesCount == 6) 1f else 1.2f)
                )
            }

        var logoWidth = boardLeftX - 4 * indent
        if (logoWidth < 0f)
            logoWidth = 0f
        var logoHeight = ctx.viewportHeight - boardTopY - 3 * indent
        if (logoHeight < 0f)
            logoHeight = 0f
        if (logoWidth > logoHeight)
            ctx.fitToRect(sLogo, logoWidth, ctx.viewportHeight)
        else
            ctx.fitToRect(sLogo, ctx.viewportWidth, logoHeight)
        sLogo.setPosition(0f, ctx.viewportHeight - sLogo.height)
        with(sRotateLeft) {
            setSize(rotateButtonSize, rotateButtonSize)
            setPosition(rotateLeftX, rotateButtonY)
        }
        with(sRotateRight) {
            setSize(rotateButtonSize, rotateButtonSize)
            setPosition(rotateRightX, rotateButtonY)
        }
        ctx.score.setCoords(
            tileHeight / 3, buttonsBaseY - indent - 2 * lineWidth,
            sRotateLeft.x + sRotateLeft.width - 3 * baseWidth, sRotateRight.x, baseWidth * 3f
        )
        with(sPlay) {
            setSize(lowerButtonSize, lowerButtonSize)
            setPosition(leftButtonsX, lowerButtonY)
        }
        with(sHome) {
            setSize(lowerButtonSize, lowerButtonSize)
            setPosition(rightButtonsX, lowerButtonY)
        }
        with(sHelp) {
            setSize(lowerButtonSize, lowerButtonSize)
            setPosition(leftButtonsX + bottomButtonsXOffset, lowerButtonY - bottomButtonsYOffset)
        }
        with(sExit) {
            setSize(lowerButtonSize, lowerButtonSize)
            setPosition(rightButtonsX - bottomButtonsXOffset, lowerButtonY - bottomButtonsYOffset)
        }
    }

    /**
     * Hit test. Determines which of the screen areas has been pressed/clicked
     */
    fun pressedArea(x: Float, y: Float): PressedArea {
        if (x > centerX - circleRadius && x < centerX + circleRadius && y < circleY + circleRadius
            && y > circleY - circleRadius
        )
            return PressedArea.NextTile

        if (x > sDown.x && x < sDown.x + sDown.width && y < sDown.y + sDown.height && y > sDown.y)
            return PressedArea.UndoMove
        if (x > sPlay.x && x < sPlay.x + sPlay.width && y < sPlay.y + sPlay.height && y > sPlay.y)
            return PressedArea.Play
        if (x > sHome.x && x < sHome.x + sHome.width && y < sHome.y + sHome.height && y > sHome.y)
            return PressedArea.Home
        if (x > sHelp.x && x < sHelp.x + sHelp.width && y < sHelp.y + sHelp.height && y > sHelp.y)
            return PressedArea.Help
        if (x > sExit.x && x < sExit.x + sDown.width && y < sExit.y + sExit.height && y > sExit.y)
            return PressedArea.Exit

        if (x > sRotateLeft.x && x < centerX && y < sRotateLeft.y + sRotateLeft.height && y > sRotateLeft.y)
            return PressedArea.RotateLeft
        if (x > centerX && x < sRotateRight.x + sRotateRight.width && y < sRotateRight.y + sRotateRight.height
            && y > sRotateRight.y
        )
            return PressedArea.RotateRight
        if (y >= circleY + circleRadius)
            return PressedArea.Board
        return PressedArea.None
    }

    /**
     * Render controls area. Render is called very often, so do not create any object here and precalculate everything.
     */
    fun render(noMoreMoves: Boolean, noLastMove: Boolean) {
        if (sLogo.width >= 2 * tileWidth || sLogo.height >= tileHeight)
            sLogo.draw(ctx.batch, 0.5f)

        sRotateLeft.draw(ctx.batch, 0.8f)
        sRotateRight.draw(ctx.batch, 0.8f)

        with(ctx.drw.sd) {
            filledCircle(centerX, circleY, circleRadius, ctx.drw.theme.gameboardBackground)
            setColor(if (noMoreMoves) ctx.drw.theme.nextTileCircleNoMoves else ctx.drw.theme.nextTileCircleOK)
            circle(
                centerX,
                circleY,
                circleRadius,
                if (noMoreMoves) lineWidth * 2 else lineWidth
            )
            if (noMoreMoves) {
                setColor(ctx.drw.theme.nextGamePrompt)
                filledCircle(
                    sPlay.x + sPlay.width / 2,
                    sPlay.y + sPlay.height / 2,
                    sPlay.height * 0.6f
                )
            }
        }

        sPlay.draw(ctx.batch, 0.8f)
        sHelp.draw(ctx.batch, 0.8f)
        sHome.draw(ctx.batch, 0.8f)
        sExit.draw(ctx.batch, 0.8f)
        sDown.draw(ctx.batch, if (noLastMove) 0.4f else 0.8f)
    }
}