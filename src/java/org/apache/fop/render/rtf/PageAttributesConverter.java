/*
 * Copyright 1999-2004 The Apache Software Foundation.
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


package org.apache.fop.render.rtf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SimpleLog;
import org.apache.fop.datatypes.Length;
import org.apache.fop.fo.Constants;
import org.apache.fop.fo.expr.NumericOp;
import org.apache.fop.fo.pagination.RegionBA;
import org.apache.fop.fo.pagination.RegionBody;
import org.apache.fop.fo.pagination.SimplePageMaster;
import org.apache.fop.fo.properties.CommonMarginBlock;
import org.apache.fop.render.rtf.rtflib.rtfdoc.RtfAttributes;
import org.apache.fop.render.rtf.rtflib.rtfdoc.RtfPage;


/**  Converts simple-page-master attributes into strings as defined in RtfPage.
 *  @author Christopher Scott, scottc@westinghouse.com
 *  @author Peter Herweg, pherweg@web.de
 */

class PageAttributesConverter {

    private static Log log = new SimpleLog("FOP/RTF");

    /** convert xsl:fo attributes to RTF text attributes */
    static RtfAttributes convertPageAttributes(SimplePageMaster pagemaster) {
        FOPRtfAttributes attrib = new FOPRtfAttributes();
        
        try {
            RegionBA before = (RegionBA) pagemaster.getRegion(Constants.FO_REGION_BEFORE);
            RegionBody body   = (RegionBody) pagemaster.getRegion(Constants.FO_REGION_BODY);
            RegionBA after  = (RegionBA) pagemaster.getRegion(Constants.FO_REGION_AFTER);
            
            attrib.set(RtfPage.PAGE_WIDTH, pagemaster.getPageWidth());
            attrib.set(RtfPage.PAGE_HEIGHT, pagemaster.getPageHeight());
            
            Length pageTop = pagemaster.getCommonMarginBlock().marginTop;
            Length pageBottom = pagemaster.getCommonMarginBlock().marginBottom;
            Length pageLeft = pagemaster.getCommonMarginBlock().marginLeft;
            Length pageRight = pagemaster.getCommonMarginBlock().marginRight;

            Length bodyTop = pageTop;
            Length bodyBottom = pageBottom;
            Length bodyLeft = pageLeft;
            Length bodyRight = pageRight;

            if (body != null) {
                // Should perhaps be replaced by full reference-area handling.
                CommonMarginBlock bodyMargin = body.getCommonMarginBlock();
                bodyTop = (Length) NumericOp.addition(pageTop, bodyMargin.marginTop);
                bodyBottom = (Length) NumericOp.addition(pageBottom, bodyMargin.marginBottom);
                bodyLeft = (Length) NumericOp.addition(pageLeft, bodyMargin.marginLeft);
                bodyRight = (Length) NumericOp.addition(pageRight, bodyMargin.marginRight);
            }
            
            attrib.set(RtfPage.MARGIN_TOP, bodyTop);
            attrib.set(RtfPage.MARGIN_BOTTOM, bodyBottom);
            attrib.set(RtfPage.MARGIN_LEFT, bodyLeft);
            attrib.set(RtfPage.MARGIN_RIGHT, bodyRight);

            //region-before attributes
            Length beforeTop = pageTop;
            if (before != null) {
                beforeTop = (Length) NumericOp.addition(pageTop, before.getExtent());
            }
            attrib.set(RtfPage.HEADERY, beforeTop);

            //region-after attributes
            Length afterBottom = pageBottom;
            if (after != null) {
                afterBottom = (Length) NumericOp.addition(pageBottom, after.getExtent());
            }
            attrib.set(RtfPage.FOOTERY, beforeTop);
        } catch (Exception e) {
            log.error("Exception in convertPageAttributes: " 
                + e.getMessage() + "- page attributes ignored");
            attrib = new FOPRtfAttributes();
        }

        return attrib;
    }
}
