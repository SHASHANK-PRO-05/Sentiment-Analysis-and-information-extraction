import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.json.simple.JSONObject;
import javax.swing.JScrollPane;

public class Gui extends JFrame {

	private JPanel contentPane;
	private JTextField query;
	private ButtonGroup outerButtonGroup = new ButtonGroup();
	private ButtonGroup innerButtonGroup = new ButtonGroup();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui frame = new Gui();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Gui() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				"C:\\Users\\Himanshu\\Desktop\\download.jpg"));
		setType(Type.POPUP);
		setTitle("IMDB/Twitter");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		query = new JTextField();
		query.setBounds(127, 11, 189, 20);
		contentPane.add(query);
		query.setColumns(10);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 108, 414, 143);
		contentPane.add(scrollPane);

		final JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setEditable(false);

		final JRadioButton twitterRB = new JRadioButton("Search topic");
		twitterRB.setBounds(264, 52, 109, 23);
		contentPane.add(twitterRB);

		final JRadioButton movieAnalyse = new JRadioButton(
				"Analyse Movie Reviews");
		movieAnalyse.setBounds(44, 78, 137, 23);
		movieAnalyse.getModel().setEnabled(false);
		contentPane.add(movieAnalyse);

		final JRadioButton reviewAnalyse = new JRadioButton(
				"Analyse Cast Reviews");
		reviewAnalyse.setBounds(264, 78, 137, 23);
		contentPane.add(reviewAnalyse);
		reviewAnalyse.getModel().setEnabled(false);

		final JRadioButton imdbRB = new JRadioButton("Search Movie");
		imdbRB.setBounds(46, 52, 109, 23);
		contentPane.add(imdbRB);

		outerButtonGroup.add(imdbRB);
		outerButtonGroup.add(twitterRB);

		imdbRB.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (imdbRB.isSelected() == true) {
					// movieAnalyse.getModel().setEnabled(true);
					// reviewAnalyse.getModel().setEnabled(true);
					// Crawler cr = new Crawler();
					// textArea.setText("Processing please wait");
					// JSONObject result =
					// cr.getCast(query.getText().toString());
					// String str = (result.get("result").toString());
					// String[] arr = str.split("/,,");
					// textArea.setText(arr.length + "");
					System.out.println("I");
				} else {
					movieAnalyse.getModel().setEnabled(false);
					reviewAnalyse.getModel().setEnabled(false);
				}
			}
		});

		twitterRB.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {

				if (twitterRB.isSelected()) {
					System.out.println("T");
					// textArea.setText("Processing please wait");
					// twitterMovieAnalyser tw = new twitterMovieAnalyser();
					// textArea.setText("" +
					// tw.get(query.getText().toString()));
				}
			}
		});
	}
}
