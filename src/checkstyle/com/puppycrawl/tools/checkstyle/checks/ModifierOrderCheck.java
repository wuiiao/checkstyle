////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2003  Oliver Burn
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.checks;

import antlr.collections.AST;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.DetailAST;

/**
 * <p>
 * Checks that the order of modifiers conforms to the suggestions in the
 * <a
 * href="http://java.sun.com/docs/books/jls/second_edition/html/classes.doc.html">
 * Java Language specification, sections 8.1.1, 8.3.1 and 8.4.3</a>.
 * The correct order is:</p>

<ol>
  <li><span class="code">public</span></li>
  <li><span class="code">protected</span></li>

  <li><span class="code">private</span></li>
  <li><span class="code">abstract</span></li>
  <li><span class="code">static</span></li>
  <li><span class="code">final</span></li>
  <li><span class="code">transient</span></li>
  <li><span class="code">volatile</span></li>

  <li><span class="code">synchronized</span></li>
  <li><span class="code">native</span></li>
  <li><span class="code">strictfp</span></li>
</ol>
 * <p>
 * Rationale: Code is easier to read if everybody follows
 * a standard.
 * </p>
 * <p>
 * An example of how to configure the check is:
 * </p>
 * <pre>
 * &lt;module name="ModifierOrder"/&gt;
 * </pre>
 * @author Lars K�hne
 */
public class ModifierOrderCheck
    extends Check
{
    /**
     * The order of modifiers as suggested in sections 8.1.1,
     * 8.3.1 and 8.4.3 of the JLS.
     */
    private static final String[] JLS_ORDER =
    {
        "public", "protected", "private", "abstract", "static", "final",
        "transient", "volatile", "synchronized", "native", "strictfp",
    };

    /** @see com.puppycrawl.tools.checkstyle.api.Check */
    public int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.MODIFIERS};
    }

    /** @see com.puppycrawl.tools.checkstyle.api.Check */
    public void visitToken(DetailAST aAST)
    {
        final List mods = new ArrayList();
        AST modifier = aAST.getFirstChild();
        while (modifier != null) {
            mods.add(modifier);
            modifier = modifier.getNextSibling();
        }

        if (!mods.isEmpty()) {
            final DetailAST error = checkOrderSuggestedByJLS(mods);
            if (error != null) {
                log(error.getLineNo(), error.getColumnNo(),
                        "mod.order", error.getText());
            }
        }
    }


    /**
     * Checks if the modifiers were added in the order suggested
     * in the Java language specification.
     *
     * @param aModifiers list of modifier AST tokens
     * @return null if the order is correct, otherwise returns the offending
     * *       modifier AST.
     */
    DetailAST checkOrderSuggestedByJLS(List aModifiers)
    {
        int i = 0;
        DetailAST modifier;
        final Iterator it = aModifiers.iterator();
        do {
            if (!it.hasNext()) {
                return null;
            }

            modifier = (DetailAST) it.next();
            while ((i < JLS_ORDER.length)
                   && !JLS_ORDER[i].equals(modifier.getText()))
            {
                i++;
            }
        } while (i < JLS_ORDER.length);

        return modifier;
    }
}
