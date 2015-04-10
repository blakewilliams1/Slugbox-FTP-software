import java.awt.Component;
import java.io.IOException;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

public class IconListRenderer 
	extends DefaultListCellRenderer {
	private static final long serialVersionUID = 1L;

	public IconListRenderer() {
	}
	
	@Override
	public Component getListCellRendererComponent(
		@SuppressWarnings("rawtypes") 
		JList list, Object value, int index, 
		boolean isSelected, boolean cellHasFocus) {
		
		// Get the renderer component from parent class
		
		JLabel label = 
			(JLabel) super.getListCellRendererComponent(list, 
				value, index, isSelected, cellHasFocus);
		
		// Get icon to use for the list item value
			
		// Set icon to display for value
		try {
			if(MainWindow.fileAtrib.lstat(MainWindow.currentServerDir.trim()+MainWindow.newList[index]).isDirectory()){
				label.setIcon(MainWindow.folderIcon);
			}else{
				label.setIcon(MainWindow.fileIcon);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return label;
	}
}
