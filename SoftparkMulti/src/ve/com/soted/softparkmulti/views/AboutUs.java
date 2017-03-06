package ve.com.soted.softparkmulti.views;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class AboutUs extends JFrame {

	JButton buttonAccept;

	public AboutUs() {

		this.setSize(400, 400);

		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();

		int x = (dim.width / 2) - (this.getWidth() / 2);
		int y = (dim.height / 2) - (this.getHeight() / 2);

		this.setLocation(x, y);

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setTitle("Acerca de");

		JPanel thePanel = new JPanel();

		thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.PAGE_AXIS));

		buttonAccept = new JButton("Aceptar");

		buttonAccept.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				dispose();

			}

		});

		thePanel.add(buttonAccept);

		this.add(thePanel);

		this.setVisible(true);

	}

}
