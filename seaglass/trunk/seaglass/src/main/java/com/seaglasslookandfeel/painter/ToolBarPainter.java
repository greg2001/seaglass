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
package com.seaglasslookandfeel.painter;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import com.seaglasslookandfeel.painter.AbstractRegionPainter.PaintContext.CacheMode;
import com.seaglasslookandfeel.state.State;
import com.seaglasslookandfeel.state.ToolBarHasNorthToolBarState;
import com.seaglasslookandfeel.util.PlatformUtils;

/**
 * ToolBarPainter implementation.
 * 
 * Parts taken from Nimbus to draw drag handle.
 * 
 * @author Ken Orr
 * @author Modified by Kathryn Huxtable for SeaGlass
 */
public class ToolBarPainter extends AbstractRegionPainter {
    public static enum Which {
        BORDER_NORTH,
        BORDER_SOUTH,
        BORDER_EAST,
        BORDER_WEST,
        BORDER_NORTH_ENABLED,
        BORDER_SOUTH_ENABLED,
        BORDER_EAST_ENABLED,
        BORDER_WEST_ENABLED,
    };

    private static final boolean IS_NON_MAC              = !PlatformUtils.isMac();
    private static final boolean IS_SNOW_LEOPARD         = PlatformUtils.isSnowLeopard();

    // For non-Mac use Snow Leopard colors because it has the same Gamma
    // correction.
    private static final Color   ACTIVE_TOP_COLOR_T      = IS_NON_MAC ? new Color(0x466c97) : IS_SNOW_LEOPARD ? new Color(0xc9c9c9)
                                                                 : new Color(0xbcbcbc);
    private static final Color   ACTIVE_TOP_COLOR_B      = IS_NON_MAC ? new Color(0x466c97) : IS_SNOW_LEOPARD ? new Color(0xb7b7b7)
                                                                 : new Color(0x9a9a9a);
    private static final Color   INACTIVE_TOP_COLOR_T    = IS_NON_MAC || IS_SNOW_LEOPARD ? new Color(0xe9e9e9) : new Color(0xe4e4e4);
    private static final Color   INACTIVE_TOP_COLOR_B    = IS_NON_MAC || IS_SNOW_LEOPARD ? new Color(0xe0e0e0) : new Color(0xd1d1d1);

    private static final Color   ACTIVE_BOTTOM_COLOR_T   = IS_NON_MAC ? new Color(0x5f80a5) : IS_SNOW_LEOPARD ? new Color(0x999999)
                                                                 : new Color(0xcccccc);
    private static final Color   ACTIVE_BOTTOM_COLOR_B   = IS_NON_MAC ? new Color(0x466c97) : IS_SNOW_LEOPARD ? new Color(0x909090)
                                                                 : new Color(0xa7a7a7);
    private static final Color   INACTIVE_BOTTOM_COLOR_T = IS_NON_MAC || IS_SNOW_LEOPARD ? new Color(0xcfcfcf) : new Color(0xe9e9e9);
    private static final Color   INACTIVE_BOTTOM_COLOR_B = IS_NON_MAC || IS_SNOW_LEOPARD ? new Color(0xcacaca) : new Color(0xd8d8d8);

    private static final State   hasNorthToolBarState    = new ToolBarHasNorthToolBarState();

    // Refers to one of the static final ints above
    private Which                state;
    private PaintContext         ctx;

    public ToolBarPainter(Which state) {
        super();
        this.state = state;
        this.ctx = new PaintContext(CacheMode.NO_CACHING);
    }

    protected void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {
        if (PlatformUtils.isMac()) {
            paintBackground(g, c, width, height);
        }
    }

    protected PaintContext getPaintContext() {
        return ctx;
    }

    private void paintBackground(Graphics2D g, JComponent c, int width, int height) {
        g.setPaint(decodeGradient(state, c, width, height));
        g.fillRect(0, 0, width, height);
    }

    private GradientPaint decodeGradient(Which state, JComponent c, int width, int height) {
        switch (state) {
        case BORDER_NORTH:
        case BORDER_NORTH_ENABLED:
        case BORDER_SOUTH:
        case BORDER_SOUTH_ENABLED:
            return new GradientPaint(0, 0, getTopColor(state, null), 0, height, getBottomColor(state));
        case BORDER_EAST:
        case BORDER_EAST_ENABLED:
        case BORDER_WEST:
        case BORDER_WEST_ENABLED:
            return new GradientPaint(0, 0, getTopColor(state, c), width, 0, getBottomColor(state));
        }
        return null;
    }

    private Color getTopColor(Which state, JComponent c) {
        switch (state) {
        case BORDER_NORTH:
            return INACTIVE_TOP_COLOR_T;
        case BORDER_SOUTH:
            return INACTIVE_BOTTOM_COLOR_T;
        case BORDER_EAST:
        case BORDER_WEST:
            if (hasNorthToolBarState.isInState(c)) {
                return INACTIVE_TOP_COLOR_B;
            }
            return INACTIVE_TOP_COLOR_T;
        case BORDER_NORTH_ENABLED:
            return ACTIVE_TOP_COLOR_T;
        case BORDER_SOUTH_ENABLED:
            return ACTIVE_BOTTOM_COLOR_T;
        case BORDER_EAST_ENABLED:
        case BORDER_WEST_ENABLED:
            if (hasNorthToolBarState.isInState(c)) {
                return ACTIVE_TOP_COLOR_B;
            }
            return ACTIVE_TOP_COLOR_T;
        }
        return ACTIVE_TOP_COLOR_T;
    }

    private Color getBottomColor(Which state) {
        switch (state) {
        case BORDER_NORTH:
            return INACTIVE_TOP_COLOR_B;
        case BORDER_SOUTH:
            return INACTIVE_BOTTOM_COLOR_B;
        case BORDER_EAST:
        case BORDER_WEST:
            return INACTIVE_BOTTOM_COLOR_T;
        case BORDER_NORTH_ENABLED:
            return ACTIVE_TOP_COLOR_B;
        case BORDER_SOUTH_ENABLED:
            return ACTIVE_BOTTOM_COLOR_B;
        case BORDER_EAST_ENABLED:
        case BORDER_WEST_ENABLED:
            return ACTIVE_BOTTOM_COLOR_T;
        }
        return ACTIVE_TOP_COLOR_B;
    }
}
