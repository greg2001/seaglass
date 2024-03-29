/*
 * Copyright (c) 2009 Kathryn Huxtable and Kenneth Orr.
 *
 * This file is part of the SeaGlass Pluggable Look and Feel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * $Id$
 */
package com.seaglass.painter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import com.seaglass.effect.DropShadowEffect;
import com.seaglass.effect.Effect;
import com.seaglass.effect.SeaGlassDropShadowEffect;
import com.seaglass.painter.AbstractRegionPainter.PaintContext.CacheMode;

/**
 * Button painter. This paints both regular and toggle buttons because they look
 * the same except for the state.
 * 
 * @author Kathryn Huxtable
 */
public final class ButtonPainter extends AbstractRegionPainter {
    public static enum Which {
        BACKGROUND_DEFAULT,
        BACKGROUND_DEFAULT_FOCUSED,
        BACKGROUND_MOUSEOVER_DEFAULT,
        BACKGROUND_MOUSEOVER_DEFAULT_FOCUSED,
        BACKGROUND_PRESSED_DEFAULT,
        BACKGROUND_PRESSED_DEFAULT_FOCUSED,
        BACKGROUND_DISABLED,
        BACKGROUND_ENABLED,
        BACKGROUND_FOCUSED,
        BACKGROUND_MOUSEOVER,
        BACKGROUND_MOUSEOVER_FOCUSED,
        BACKGROUND_PRESSED,
        BACKGROUND_PRESSED_FOCUSED,
        BACKGROUND_SELECTED,
        BACKGROUND_SELECTED_FOCUSED,
        BACKGROUND_PRESSED_SELECTED,
        BACKGROUND_PRESSED_SELECTED_FOCUSED,
        BACKGROUND_DISABLED_SELECTED
    };

    private enum SegmentStatus {
        NONE, FIRST, MIDDLE, LAST
    };

    private static final Insets    insets      = new Insets(7, 7, 7, 7);
    private static final Dimension dimension   = new Dimension(86, 29);
    private static final CacheMode cacheMode   = CacheMode.NINE_SQUARE_SCALE;
    private static final Double    maxH        = Double.POSITIVE_INFINITY;
    private static final Double    maxV        = Double.POSITIVE_INFINITY;

    private final Color            colorShadow = new Color(0x000000);
    private final Color            colorFocus  = new Color(0x79a0cf);

    private final ButtonVariants   normal      = new ButtonVariants(
                                               // Enabled
                                                   new ButtonStateColors(new Color(0xf3ffffff, true), new Color(0x00ffffff, true),
                                                       new Color(0x00f7fcff, true), new Color(0xffffffff, true), 0.5f, new Color(0xa8d2f2),
                                                       new Color(0x88ade0), new Color(0x5785bf)),
                                                   // Enabled+Pressed
                                                   new ButtonStateColors(new Color(0xb3eeeeee, true), new Color(0x00ffffff, true),
                                                       new Color(0x00A8D9FC, true), new Color(0xffb4d9ee, true), 0.4f, new Color(0x134D8C),
                                                       new Color(0x4F7BBF), new Color(0x3F76BF)),
                                                   // Default
                                                   new ButtonStateColors(new Color(0xc0ffffff, true), new Color(0x00eeeeee, true),
                                                       new Color(0x00A8D9FC, true), new Color(0xffC0E8FF, true), 0.4f, new Color(0x276FB2),
                                                       new Color(0x4F7BBF), new Color(0x3F76BF)),
                                                   // Default+Pressed
                                                   new ButtonStateColors(new Color(0xc0eeeeee, true), new Color(0x00eeeeee, true),
                                                       new Color(0x00A8D9FC, true), new Color(0xffB4D9EE, true), 0.4f, new Color(0x134D8C),
                                                       new Color(0x4F7BBF), new Color(0x3F76BF)),
                                                   // Disabled
                                                   new ButtonStateColors(new Color(0xc0F4F8FB, true), new Color(0x00ffffff, true),
                                                       new Color(0x00A8D9FC, true), new Color(0xffF7FCFF, true), 0.4f, new Color(0xeeeeee),
                                                       new Color(0x8AAFE0), new Color(0x5785BF)),
                                                   // Disabled+Selected
                                                   new ButtonStateColors(new Color(0xc0F4F8FB, true), new Color(0x00ffffff, true),
                                                       new Color(0x00A8D9FC, true), new Color(0xffF7FCFF, true), 0.4f, new Color(0xaaaaaa),
                                                       new Color(0x8AAFE0), new Color(0x5785BF)));

    private final ButtonVariants   textured    = new ButtonVariants(
                                               // Enabled
                                                   new ButtonStateColors(new Color(0xf3ffffff, true), new Color(0x00ffffff, true),
                                                       new Color(0, true), new Color(0, true), 0.5f, new Color(0xbbbbbb),
                                                       new Color(0x555555), new Color(0x4c4c4c)),
                                                   // Enabled+Pressed
                                                   new ButtonStateColors(new Color(0, true), new Color(0, true),
                                                       new Color(0x00888888, true), new Color(0xffcccccc, true), 0.5f, new Color(0x777777),
                                                       new Color(0x555555), new Color(0x4c4c4c)),
                                                   // Default
                                                   new ButtonStateColors(new Color(0xf3ffffff, true), new Color(0x00ffffff, true),
                                                       new Color(0, true), new Color(0, true), 0.5f, new Color(0x999999),
                                                       new Color(0x555555), new Color(0x4c4c4c)),
                                                   // Default+Pressed
                                                   new ButtonStateColors(new Color(0, true), new Color(0, true),
                                                       new Color(0x00888888, true), new Color(0xffcccccc, true), 0.5f, new Color(0x777777),
                                                       new Color(0x555555), new Color(0x4c4c4c)),
                                                   // Disabled
                                                   new ButtonStateColors(new Color(0xf3ffffff, true), new Color(0x00ffffff, true),
                                                       new Color(0, true), new Color(0, true), 0.5f, new Color(0xbbbbbb),
                                                       new Color(0x555555), new Color(0x4c4c4c)),
                                                   // Disabled+Selected
                                                   new ButtonStateColors(new Color(0xf3ffffff, true), new Color(0x00ffffff, true),
                                                       new Color(0, true), new Color(0, true), 0.5f, new Color(0xaaaaaa),
                                                       new Color(0x555555), new Color(0x4c4c4c)));

    private Path2D                 path        = new Path2D.Double();

    private Which                  state;
    private boolean                focused;
    private Effect                 dropShadow  = new SeaGlassDropShadowEffect();

    private PaintContext           ctx;

    /**
     * Create a new ButtonPainter.
     * 
     * @param ctx
     *            the PaintContext to be used.
     * @param state
     *            the state of the button to be painte   */
    pthis.state = state;
        switch (state) {
        case BACKGROUND_DEFAULT_FOCUSED:
        case BACKGROUND_MOUSEOVER_DEFAULT_FOCUSED:
        case BACKGROUND_PRESSED_DEFAULT_FOCUSED:
        case BACKGROUND_FOCUSED:
        case BACKGROUND_MOUSEOVER_FOCUSED:
        case BACKGROUND_PRESSED_FOCUSED:
        case BACKGROUND_SELECTED_FOCUSED:
        case BACKGROUND_PRESSED_SELECTED_FOCUSED:
            focused = true;
            break;
        default:
            focused = false;
            break;
        } 
        ctx = new PaintContext(insets, dimension, false, cacheMode, maxH, maxV);
    }

    protected Object[] getExtendedCacheKeys(JComponent c) {
        Object[] extendedCacheKeys = new Object[] {};
        return extendedCacheKeys;
    }

    protected final PaintContext getPaintContext() {
        return ctx;
    }
't specify.
        return "button_enabled";
    }

    @Override
    protected void doPaint(Graphics2D g, JComponentObject buttonType = c.getClientProperty("JButton.buttonType");
        ButtonVariants buttonVariants = normal;
        if ("segmentedTextured".equals(buttonType)) {
            buttonVariants = textured;
        }

        SegmentStatus segmentStatus = SegmentStatus.NONE;
        if (buttonType != null && buttonType instanceof String && ((String) buttonType).startsWith("segmented" if ("segmented".equals(c.getClientProperty("JButton.buttonType"))) {
            String position = (String) c.getClientProperty("JButton.segmentPosition");
  Status = SegmentStatus.FIRST;
            } else if ("middle".equals(position)) {
                segmentStatus = SegmentStatus.MIDDLE;
            } else if ("last".equals(position)) {
                segmentStatus = SegmentStatus.LAST      segmentedLast.paintI
        switch (state) {
        case BACKGROUND_DEFAULT:
        case BACKGROUND_DEFAULT_FOCUSED:
        case BACKGROUND_MOUSEOVER_DEFAULT:
        case BACKGROUND_MOUSEOVER_DEFAULT_FOCUSED:
        case BACKGROUND_SELECTED:
        case BACKGROUND_SELECTED_FOCUSED:
            drawButton(g, c, width, height, segmentStatus, buttonVariants.defaultButton);
            break;
        case BACKGROUND_PRESSED_DEFAULT:
        case BACKGROUND_PRESSED_DEFAULT_FOCUSED:
            drawButton(g, c, width, height, segmentStatus, buttonVariants.defaultPressed);
            break;
        case BACKGROUND_DISABLED:
            drawButton(g, c, width, height, segmentStatus, buttonVariants.disabled);
            break;
        case BACKGROUND_ENABLED:
        case BACKGROUND_FOCUSED:
        case BACKGROUND_MOUSEOVER:
        case BACKGROUND_MOUSEOVER_FOCUSED:
            drawButton(g, c, width, height, segmentStatus, buttonVariants.enabled);
            break;
        case BACKGROUND_PRESSED:
        case BACKGROUND_PRESSED_FOCUSED:
        case BACKGROUND_PRESSED_SELECTED:
        case BACKGROUND_PRESSED_SELECTED_FOCUSED:
            drawButton(g, c, width, height, segmentStatus, buttonVariants.enabledPressed);
            break;
        case BACKGROUND_DISABLED_SELECTED:
            drawButton(g, c, width, height, segmentStatus, buttonVariants.disabledSelected);
            break;
        }
    }

    /*
     * Draw a button.
     */
    private void drawButton(Graphics2D g, JComponent c, int width, int height, SegmentStatus segmentStatus, ButtonStateColors bsc) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        path = decodeRoundBackground(segmentStatus);
        g.drawImage(createDropShadowImage(path), 0, 0, null);
        g.setPaint(decodeGradientBackground(path, bsc.backgroundTop, bsc.backgroundBottom));
        g.fill(path);
        path = decodeRoundMain(segmentStatus);
        g.setColor(bsc.color);
        g.fill(path);
        g.setPaint(decodeGradientBottomShine(path, bsc.lowerShineTop, bsc.lowerShineBottom, bsc.lowerShineMidpoint));
        g.fill(path);
        g.setPaint(decodeGradientTopShine(path, bsc.upperShineTop, bsc.upperShineBottom));
        g.fill(path);
        if (focused) {
            path = decodeRoundFocus(segmentStatus);
            g.setColor(colorFocus);
            g.setStroke(new BasicStroke(1.5f));
            g.draw(path);
        }
    }

    /**
     * @param g
     */
    private BufferedImage createDropShadowImage(Shape s) {
        BufferedImage bimage = DropShadowEffect.createBufferedImage(dimension.width, dimension.height, true);
        Graphics2D gbi = bimage.createGraphics();
        gbi.setColor(colorShadow);
        gbi.fill(s);
        return dropShadow.applyEffect(bimage, null, dimension.width, dimension.height);
    }

    private Path2D decodeRoundFocus(SegmentStatus segmentStatus) {
        double arcSize = 5.25d;
        switch (segmentStatus) {
        case FIRST:
            setFirstRoundRect(0d, 0d, 85d, 27d, arcSize, arcSize);
            break;
        case MIDDLE:
            setRect(0d, 0d, 85d, 27d);
            break;
        case LAST:
            setLastRoundRect(0d, 0d, 85d, 27d, arcSize, arcSize);
            break;
        default:
            setRoundRect(0d, 0d, 85d, 27d, arcSize, arcSize);
            break;
        }
        return path;
    }

    private Path2D decodeRoundBackground(SegmentStatus segmentStatus) {
        double arcSize = 4d;
        switch (segmentStatus) {
        case FIRST:
            setFirstRoundRect(1d, 1d, 85d, 26d, arcSize, arcSize);
            break;
        case MIDDLE:
            setRect(0d, 1d, 86d, 26d);
            break;
        case LAST:
            setLastRoundRect(0d, 1d, 85d, 26d, arcSize, arcSize);
            break;
        default:
            setRoundRect(1d, 1d, 84d, 26d, arcSize, arcSize);
            break;
        }
        return path;
    }

    private Path2D decodeRoundMain(SegmentStatus segmentStatus) {
        double arcSize = 3d;
        switch (segmentStatus) {
        case FIRST:
            setFirstRoundRect(2d, 2d, 83d, 24d, arcSize, arcSize);
            break;
        case MIDDLE:
            setRect(0d, 2d, 85d, 24d);
            break;
        case LAST:
            setLastRoundRect(0d, 2d, 84d, 24d, arcSize, arcSize);
            break;
        default:
            setRoundRect(2d, 2d, 82d, 24d, arcSize, arcSize);
            break;
        }
        return path;
    }

    private void setRoundRect(Double left, Double top, Double width, Double height, Double arcW, Double arcH) {
        Double bottom = top + height;
        Double right = left + width;
        path.reset();
        path.moveTo(left + arcW, top);
        path.quadTo(left, top, left, top + arcH);
        path.lineTo(left, bottom - arcH);
        path.quadTo(left, bottom, left + arcW, bottom);
        path.lineTo(right - arcW, bottom);
        path.quadTo(right, bottom, right, bottom - arcH);
        path.lineTo(right, top + arcH);
        path.quadTo(right, top, right - arcW, top);
        path.closePath();
    }

    private void setFirstRoundRect(Double left, Double top, Double width, Double height, Double arcW, Double arcH) {
        Double bottom = top + height;
        Double right = left + width;
        path.reset();
        path.moveTo(left + arcW, top);
        path.quadTo(left, top, left, top + arcH);
        path.lineTo(left, bottom - arcH);
        path.quadTo(left, bottom, left + arcW, bottom);
        path.lineTo(right, bottom);
        path.lineTo(right, top);
        path.closePath();
    }

    private void setLastRoundRect(Double left, Double top, Double width, Double height, Double arcW, Double arcH) {
        Double bottom = top + height;
        Double right = left + width;
        path.reset();
        path.moveTo(left, top);
        path.lineTo(left, bottom);
        path.lineTo(right - arcW, bottom);
        path.quadTo(right, bottom, right, bottom - arcH);
        path.lineTo(right, top + arcH);
        path.quadTo(right, top, right - arcW, top);
        path.closePath();
    }

    private void setRect(Double left, Double top, Double width, Double height) {
        Double bottom = top + height;
        Double right = left + width;
        path.reset();
        path.moveTo(left, top);
        path.lineTo(left, bottom);
        path.lineTo(right, bottom);
        path.lineTo(right, top);
        path.closePath();
    }

    /**
     * Create the gradient for the background of the button. This creates the
     * border.
     * 
     * @param s
     * @param color1
     * @param color2
     * @return
     */
    private Paint decodeGradientBackground(Shape s, Color color1, Color color2) {
        Rectangle2D bounds = s.getBounds2D();
        float x = (float) bounds.getX();
        float y = (float) bounds.getY();
        float w = (float) bounds.getWidth();
        float h = (float) bounds.getHeight();
        return decodeGradient((0.5f * w) + x, y, (0.5f * w) + x, h + y, new float[] { 0f, 1f }, new Color[] { color1, color2 });
    }

    /**
     * Create the gradient for the shine at the bottom of the button.
     * 
     * @param color1
     * @param color2
     * @param midpoint
     */
    private Paint decodeGradientBottomShine(Shape s, Color color1, Color color2, float midpoint) {
        Color midColor = new Color(deriveARGB(color1, color2, midpoint) & 0xFFFFFF, true);
        Rectangle2D bounds = s.getBounds2D();
        float x = (float) bounds.getX();
        float y = (float) bounds.getY();
        float w = (float) bounds.getWidth();
        float h = (float) bounds.getHeight();
        return decodeGradient((0.5f * w) + x, y, (0.5f * w) + x, h + y, new float[] { 0f, midpoint, 1f }, new Color[] {
            color1,
            midColor,
            color2 });
    }

    /**
     * Create the gradient for the shine at the top of the button.
     * 
     * @param s
     * @param color1
     * @param color2
     * @return
     */
    private Paint decodeGradientTopShine(Shape s, Color color1, Color color2) {
        Rectangle2D bounds = s.getBounds2D();
        float x = (float) bounds.getX();
        float y = (float) bounds.getY();
        float w = (float) bounds.getWidth();
        float h = (float) bounds.getHeight();
        return decodeGradient((0.5f * w) + x, y, (0.5f * w) + x, h + y, new float[] { 0f, 1f }, new Color[] { color1, color2 });
    }

    private class ButtonVariants {

        public ButtonStateColors enabled;
        public ButtonStateColors enabledPressed;
        public ButtonStateColors defaultButton;
        public ButtonStateColors defaultPressed;
        public ButtonStateColors disabled;
        public ButtonStateColors disabledSelected;

        public ButtonVariants(ButtonStateColors enabled, ButtonStateColors enabledPressed, ButtonStateColors defaultButton,
            ButtonStateColors defaultPressed, ButtonStateColors disabled, ButtonStateColors disabledSelected) {
            this.enabled = enabled;
            this.enabledPressed = enabledPressed;
            this.defaultButton = defaultButton;
            this.defaultPressed = defaultPressed;
            this.disabled = disabled;
            this.disabledSelected = disabledSelected;
        }
    }

    private class ButtonStateColors {

        public Color upperShineTop;
        public Color upperShineBottom;
        public Color lowerShineTop;
        public Color lowerShineBottom;
        public float lowerShineMidpoint;
        public Color color;
        public Color backgroundTop;
        public Color backgroundBottom;

        public ButtonStateColors(Color upperShineTop, Color upperShineBottom, Color lowerShineTop, Color lowerShineBottom,
            float lowerShineMidpoint, Color color, Color backgroundTop, Color backgroundBottom) {
            this.upperShineTop = upperShineTop;
            this.upperShineBottom = upperShineBottom;
            this.lowerShineTop = lowerShineTop;
            this.lowerShineBottom = lowerShineBottom;
            this.lowerShineMidpoint = lowerShineMidpoint;
            this.color = color;
            this.backgroundTop = backgroundTop;
            this.backgroundBottom = backgroundBottom;
        }
    }
}
