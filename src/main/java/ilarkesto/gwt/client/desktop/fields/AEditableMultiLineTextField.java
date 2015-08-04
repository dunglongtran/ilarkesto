package ilarkesto.gwt.client.desktop.fields;

import ilarkesto.gwt.client.desktop.Widgets;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;

public abstract class AEditableMultiLineTextField extends AEditableField {

	private TextArea textArea;

	public abstract void applyValue(String value);

	protected abstract String getValue();

	@Override
	public boolean isValueSet() {
		return getValue() != null;
	}

	protected String prepareValue(String text) {
		return text;
	}

	protected String prepareText(String text) {
		if (text == null) return null;
		text = text.trim();
		if (text.isEmpty()) return null;
		return text;
	}

	public void validateValue(String value) throws RuntimeException {
		if (value == null && isMandatory()) throw new RuntimeException("Eingabe erforderlich.");
	}

	@Override
	public void trySubmit() throws RuntimeException {
		String text = prepareText(textArea.getText());
		String value = prepareValue(text);
		validateValue(value);
		applyValue(value);
	}

	@Override
	public TextArea createEditorWidget() {
		textArea = new TextArea();
		textArea.getElement().setId(getId() + "_textArea");
		Style style = textArea.getElement().getStyle();
		style.setWidth(getTextBoxWidth(), Unit.PX);
		style.setHeight(100, Unit.PX);
		style.setPadding(Widgets.defaultSpacing, Unit.PX);

		String value = getValue();
		if (value == null) value = getAlternateValueIfValueIsNull();
		textArea.setValue(value);

		if (getEditVetoMessage() == null) {
			textArea.addKeyUpHandler(new EnterKeyUpHandler());
		} else {
			textArea.setEnabled(false);
			textArea.setTitle(getEditVetoMessage());
		}
		return textArea;
	}

	private int getTextBoxWidth() {
		int width = Window.getClientWidth();
		if (width > 700) width = 700;
		return width;
	}

	@Override
	public IsWidget createDisplayWidget() {
		String text = getValue();

		Label label = new Label();
		if (text == null) {
			text = getAlternateValueIfValueIsNull();
			label.getElement().getStyle().setColor("#AAA");
		}

		label.setText(text);
		label.getElement().getStyle().setWhiteSpace(WhiteSpace.PRE_WRAP);
		return label;
	}

	public String getAlternateValueIfValueIsNull() {
		return null;
	}

	private class EnterKeyUpHandler implements KeyUpHandler {

		@Override
		public void onKeyUp(KeyUpEvent event) {
			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER && event.getNativeEvent().getCtrlKey()) {
				AEditableMultiFieldField parent = getParent();
				if (parent == null) {
					getFieldEditorDialogBox().submit();
				} else {
					parent.getFieldEditorDialogBox().submit();
				}
			}
		}

	}

}
