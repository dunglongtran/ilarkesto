/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.integration.itext;

import ilarkesto.core.base.Str;
import ilarkesto.pdf.ACell;
import ilarkesto.pdf.AImage;
import ilarkesto.pdf.AParagraph;
import ilarkesto.pdf.APdfBuilder;
import ilarkesto.pdf.APdfElement;
import ilarkesto.pdf.ATable;
import ilarkesto.pdf.FontStyle;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPCell;

public class Cell extends ACell implements ItextElement {

	private Collection<ItextElement> elements = new ArrayList<ItextElement>();

	Cell(APdfElement parent, FontStyle fontStyle) {
		super(parent, fontStyle);
	}

	@Override
	public AParagraph paragraph() {
		Paragraph p = new Paragraph(this, getFontStyle());
		elements.add(p);
		return p;
	}

	@Override
	public AImage image(File file) {
		Image i = new Image(this, file);
		elements.add(i);
		return i;
	}

	@Override
	public ATable table(float... cellWidths) {
		Table t = new Table(this, getFontStyle());
		t.setCellWidths(cellWidths);
		elements.add(t);
		return t;
	}

	@Override
	public ATable table(int columnCount) {
		Table t = new Table(this, getFontStyle());
		t.setColumnCount(columnCount);
		elements.add(t);
		return t;
	}

	@Override
	public AImage image(byte[] data) {
		Image i = new Image(this, data);
		elements.add(i);
		return i;
	}

	@Override
	public Element[] createITextElements(Document document) {
		PdfPCell cell = new PdfPCell();

		cell.setBorderColorTop(PdfBuilder.color(getBorderTopColor()));
		cell.setBorderColorBottom(PdfBuilder.color(getBorderBottomColor()));
		cell.setBorderColorLeft(PdfBuilder.color(getBorderLeftColor()));
		cell.setBorderColorRight(PdfBuilder.color(getBorderRightColor()));
		cell.setBorderWidthTop(APdfBuilder.mmToPoints(getBorderTopWidth()));
		cell.setBorderWidthBottom(APdfBuilder.mmToPoints(getBorderBottomWidth()));
		cell.setBorderWidthLeft(APdfBuilder.mmToPoints(getBorderLeftWidth()));
		cell.setBorderWidthRight(APdfBuilder.mmToPoints(getBorderRightWidth()));
		cell.setUseBorderPadding(false);

		cell.setPadding(0);
		cell.setPaddingTop(APdfBuilder.mmToPoints(getPaddingTop()));
		cell.setPaddingBottom(APdfBuilder.mmToPoints(getPaddingBottom()));
		cell.setPaddingLeft(APdfBuilder.mmToPoints(getPaddingLeft()));
		cell.setPaddingRight(APdfBuilder.mmToPoints(getPaddingRight()));

		cell.setBackgroundColor(PdfBuilder.color(getBackgroundColor()));
		cell.setExtraParagraphSpace(0);
		cell.setIndent(0);

		if (getVerticalAlign() != null) {
			cell.setVerticalAlignment(convertVerticalAlignment(getVerticalAlign()));
		}

		cell.setColspan(getColspan());
		for (ItextElement element : elements) {
			for (Element e : element.createITextElements(document)) {
				cell.addElement(e);
			}
		}
		return new Element[] { cell };
	}

	@Override
	public String toString() {
		return "C: " + Str.format(elements);
	}

}
