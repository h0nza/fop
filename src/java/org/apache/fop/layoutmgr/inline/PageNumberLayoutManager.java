/*
 * Copyright 1999-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id$ */

package org.apache.fop.layoutmgr.inline;

import org.apache.fop.fo.flow.PageNumber;
import org.apache.fop.area.inline.InlineArea;
import org.apache.fop.area.inline.TextArea;
import org.apache.fop.area.Trait;
import org.apache.fop.fonts.Font;
import org.apache.fop.layoutmgr.LayoutContext;
import org.apache.fop.layoutmgr.TraitSetter;

/**
 * LayoutManager for the fo:page-number formatting object
 */
public class PageNumberLayoutManager extends LeafNodeLayoutManager {
    
    private PageNumber fobj;
    private Font font;
    
    /**
     * Constructor
     *
     * @param node the fo:page-number formatting object that creates the area
     * @todo better null checking of node, font
     */
    public PageNumberLayoutManager(PageNumber node) {
        super(node);
        fobj = node;
        font = fobj.getCommonFont().getFontState(fobj.getFOEventHandler().getFontInfo());
    }

    public InlineArea get(LayoutContext context) {
        // get page string from parent, build area
        TextArea text = new TextArea();
        String str = getCurrentPV().getPageNumberString();
        int width = 0;
        for (int count = 0; count < str.length(); count++) {
            width += font.getCharWidth(str.charAt(count));
        }
        text.setTextArea(str);
        text.setIPD(width);
        text.setBPD(font.getAscender() - font.getDescender());
        text.setOffset(font.getAscender());
        text.addTrait(Trait.FONT_NAME, font.getFontName());
        text.addTrait(Trait.FONT_SIZE,
                        new Integer(font.getFontSize()));
        text.addTrait(Trait.COLOR, fobj.getColor());

        TraitSetter.addTextDecoration(text, fobj.getTextDecoration());

        return text;
    }
    
    
    /** @see org.apache.fop.layoutmgr.inline.LeafNodeLayoutManager#getLead() */
    public int getLead() {
        return font.getAscender();
    }
    
    protected void offsetArea(InlineArea area, LayoutContext context) {
        area.setOffset(context.getBaseline());
    }
    
    protected InlineArea getEffectiveArea() {
        TextArea baseArea = (TextArea)curArea;
        //TODO Maybe replace that with a clone() call or better, a copy constructor
        //TODO or even better: delay area creation until addAreas() stage
        //TextArea is cloned because the LM is reused in static areas and the area can't be.
        TextArea ta = new TextArea();
        TraitSetter.setProducerID(ta, fobj.getId());
        ta.setIPD(baseArea.getIPD());
        ta.setBPD(baseArea.getBPD());
        ta.setOffset(baseArea.getOffset());
        ta.addTrait(Trait.FONT_NAME, font.getFontName()); //only to initialize the trait map
        ta.getTraits().putAll(baseArea.getTraits());
        updateContent(ta);
        return ta;
    }
    
    private void updateContent(TextArea area) {
        area.setTextArea(getCurrentPV().getPageNumberString());
    }
    
    protected void addId() {
        getPSLM().addIDToPage(fobj.getId());
    }
}

