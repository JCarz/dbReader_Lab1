package reader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * //TODO the main JAVAFX class must extend Application class
 * 
 * @author Shahriar (Shawn) Emami
 * @version Jan 17, 2021
 */
public class DBReader extends Application {

	/**
	 * width of the scene
	 */
	private static final double WIDTH = 600;
	/**
	 * height of the scene
	 */
	private static final double HEIGHT = 400;
	/**
	 * title of the application
	 */
	private static final String TITLE = "DB Reader";
	/**
	 * names of the columns
	 */
	private static final String[] COLUMN_NAMES = { "ID", "Column 01", "Column 02", "Column 03" };

	/**
	 * {@link BorderPane} is a layout manager that manages all nodes in 5 areas as below:
	 * 
	 * <pre>
	 * -----------------------
	 * |        top          |
	 * -----------------------
	 * |    |          |     |
	 * |left|  center  |right|
	 * |    |          |     |
	 * -----------------------
	 * |       bottom        |
	 * -----------------------
	 * </pre>
	 * 
	 * this object is passed to {@link Scene} object in {@link DBReader#start(Stage)} method.
	 */
	private BorderPane root;

	private Connection connection;
	private Label connectionStatus;
	private WebView webView;
	private TextField dbURLText;
	private TextField userText;
	private TextField searchText;
	private PasswordField passText;

	/**
	 * this method is called at the very beginning of the JavaFX application and can be used to initialize all
	 * components in the application. however, {@link Scene} and {@link Stage} must not be created in this method. this
	 * method does not run JavaFX thread, it runs on JavaFX-Launcher thread.
	 */
	@Override
	public void init() throws Exception {
		Region optionsBar = createOptionsBar();
		Region statusBar = createStatusBar();
		root = new BorderPane();
		// TODO Initialize the root variable
		// TODO on the root variable call the method setTop and pass to it the return result of createOptionsBar
		root.setTop(optionsBar);
		// TODO on the root variable call the method setBottom and pass to it the return result of createStatusBar
		root.setBottom(statusBar);

	}

	/**
	 * <p>
	 * this method is called when JavaFX application is started and it is running on JavaFX thread. this method must at
	 * least create {@link Scene} and finish customizing {@link Stage}. these two objects must be on JavaFX thread when
	 * created.
	 * </p>
	 * <p>
	 * {@link Stage} represents the frame of your application, such as minimize, maximize and close buttons.<br>
	 * {@link Scene} represents the holder of all your JavaFX {@link Node}s.<br>
	 * {@link Node} is the super class of every javaFX class.
	 * </p>
	 * 
	 * @param primaryStage - primary stage of your application that will be rendered
	 */
	@Override
	public void start( Stage primaryStage) throws Exception {
		// scene holds all JavaFX components that need to be displayed in Stage
		Scene scene = new Scene( root, WIDTH, HEIGHT);
		primaryStage.setScene( scene);
		primaryStage.setTitle( TITLE);
		primaryStage.setResizable( true);
		// when escape key is pressed close the application
		primaryStage.addEventHandler( KeyEvent.KEY_RELEASED, ( KeyEvent event) -> {
			if ( KeyCode.ESCAPE == event.getCode()) {
				primaryStage.hide();
			}
		});
		// WebView must be created in start method because it must be initialized on JavaFX thread
		webView = new WebView();
		root.setCenter( webView);
		// display the JavaFX application
		primaryStage.show();
	}

	/**
	 * this method is called at the very end when the application is about to exit. this method is used to stop or
	 * release any resources used during the application.
	 */
	@Override
	public void stop() throws Exception {
		// TODO if connection is not null call close method of the connection variable
		if(connection != null)
			connection.close();
	}

	private Button createButton( String name, EventHandler< ActionEvent> onClick) {
		// TODO create a new variable of type Button from javafx and pass name as constructor argument.
		Button b1 = new Button(name);
		// TODO call method setMaxSize on the button and pass Double.MAX_VALUE as both arguments.
		b1.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		// we do this so we can stretch the button size otherwise we cannot.
		// TODO call method setOnAction on the button and pass to it onClick.
		b1.setOnAction(onClick);
		// TODO finally return the button.s
		return b1;
	}

	private TextField createTextField( String value, String prompt) {
		// TODO create a new variable of type TextField from javafx.
		TextField textField1 = new TextField(value);
		// TODO call setText on textField and pass value to it.
		textField1.setText(value);
		// TODO call setPromptText on textField and pass prompt to it.
		textField1.setPromptText(prompt);
		// TODO use the static method setHgrow in class GridPane and pass to it textField and Priority.ALWAYS.
		GridPane.setHgrow(textField1,Priority.ALWAYS);
		// we do this to allow the textField to be maximizable horizontally so we can stretch the application.
		// TODO finally return the textField.
		return textField1;
	}

	private PasswordField createPasswordField( String value, String prompt) {
		// TODO do the same thing as the method createTextField but instead use the Type PasswordField.
		PasswordField passwordField1 = new PasswordField();
		passwordField1.setText(value);
		passwordField1.setPromptText(prompt);
		GridPane.setHgrow(passwordField1, Priority.ALWAYS);
		return passwordField1;
	}

	/**
	 * create a {@link ToolBar} that will represent the status bar of the application.
	 * 
	 * @return customized {@link ToolBar} as its super class {@link Region}.
	 */
	private Region createStatusBar() {
		// TODO initialize the conectionStatus variable using Label class. pass to constructor "Not Connected".
		connectionStatus = new Label("Not Connected");
		// TODO create a new ToolBar object and pass to its constructor conectionStatus.
		ToolBar toolBar1 = new ToolBar(connectionStatus);
		// TODO return the newly created ToolBar.
		return toolBar1;
	}

	private void performConnectAction( ActionEvent e) {
		// TODO complete method
		// before calling connectTo, setText on conectionStatus pass to it "connecting".
		connectionStatus.setText("connecting");
		try {
			connectTo(dbURLText.getText(),userText.getText(),passText.getText());
			connectionStatus.setText("Connected");
		} catch (SQLException exception) {
			connectionStatus.setText("Failed: "+exception.getMessage());
		}
		// call connectTo and pass the values in the 3 TextFields you created. use getText on TextFeield to get the text out of it.
		// after calling connectTo, setText on conectionStatus pass to it "connected".
		// if exception is thrown, setText on conectionStatus pass to it "failed: " + ex.getMessage().
		// don't forget connectTo throws an exception, so use try and catch.
	}

	private void performSearchAction( ActionEvent e) {
		// TODO complete method
		connectionStatus.setText("Searching");
		try {
			ObservableList<List<String>> list = search(searchText.getText().trim());
			populateTextArea(list);
			connectionStatus.setText("Finished");
		} catch (SQLException exception) {
			connectionStatus.setText("Failed: "+exception.getMessage());
		}
		// before calling search, setText on conectionStatus pass to it "searching".
		// call search and pass searchText.getText().trim(). store the result in a variable called list.
		// after calling search, call populateTextArea( list)
		// setText on conectionStatus and pass to it "finished".
		// if exception is thrown, setText on conectionStatus pass to it "failed: " + ex.getMessage().
		// don't forget search throws an exception, so use try and catch.
	}

	/**
	 * create a {@link MenuBar} that represent the menu bar at the top of the application.
	 * 
	 * @return customized {@link MenuBar} as its super class {@link Region}.
	 */
	private Region createOptionsBar() {
		// TODO for the following instructions use the create methods above
		// ex, Button b = createButton("B", e->{/*code*/});
		dbURLText =  createTextField("test","DB URL");
		userText = createTextField("test","Username");
		searchText = createTextField("","Search Text");
		passText = createPasswordField("test","Password");
		// TODO initialize dbURLText and pass to it "test" and "DB URL"
		// TODO initialize userText and pass to it "test" and "Username"
		// TODO initialize searchText and pass to it "" and "Search Text"
		// TODO initialize passText and pass to it "test" and "Password"

		// a lambda can look like below:
		// (String a, String b)->{System.out.println(a+b);}
		// lambdas can also be simplified using the rules below:
		// rule 1: if you only have one line of code in your lambda there is no need for {}
		// (String a, String b)->System.out.println(a+b)
		// rule 2: the type of arguments can be inferred by complier, so it can be removed.
		// (a, b)->System.out.println(a+b)
		// rule 3: if you only have one argument you can skip () and also use rule 2.
		// a->System.out.println(a)
		// rule 4: if you only have one line of code, the return statement is implied.
		// (a,b)->a+b;
		// rule 5: all lambdas are objects, they can be stored in any interface with only one method
		// which have the same number of arguments and return type.
		// EventHandler< ActionEvent> onClick = e -> System.out.println("button clicked");
		// a list of already built-in one function interfaces called functional interface can be found in:
		// https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html

		// a method reference looks like:
		// this::performConnectAction
		// it needs the following signature:
		// [class/object/this]::[name of the method]
		// this is used instead of a lambda if a method we already have, has the same signature as our lambda.
		// this way there is no need to make a new lambda, a method reference can just directly point to the method instead.

		// TODO create a Button called connectButton and pass to it "Connect" and a method reference to performConnectAction.
		Button connectionButton = createButton("connect",this::performConnectAction);
		// TODO create a Button called searchButton and pass to it "Search" and a method reference to performSearchAction.
		Button searchButton = createButton("Search",this::performSearchAction);

		// TODO call setOnAction on searchText and use rule 3 to execute searchButton.fire().
		searchText.setOnAction(e-> searchButton.fire());
		// TODO create an instance of GridPane called grid.
		GridPane gridPane = new GridPane();
		// TODO on grid call methods setHgap and setVgap. pass to both 3.
		gridPane.setHgap(3);
		gridPane.setVgap(3);
		// TODO on grid call setPadding and pass to it new Insets( 5, 5, 5, 5).
		//Insets insets = new Insets(5,5,5,5);
		gridPane.setPadding(new Insets(5,5,5,5));
		// TODO use the the method GridPane::add(Node child, int columnIndex, int rowIndex, int colspan, int rowspan)
		gridPane.add( dbURLText, 0, 0, 2, 1);
		gridPane.add( userText, 0, 1, 1, 1);
		gridPane.add( passText ,1,1,1,1);
		gridPane.add( searchText , 0,2,2,1);
		gridPane.add( connectionButton, 2,0,1,2);
		// to add all the Nodes created in this method to grid object.
		// use the image in the lab 1 to find the row, col, rowspan, colspan.
		// for example grid.add( connectButton, 2, 0, 1, 2);
		// means connectButton is at column 2, row 0, it expands 1 column, and expands 2 rows.

		// TODO return grid
		return gridPane;
	}

	private void populateTextArea( ObservableList< List< String>> data) {
		// TODO create StringBuilder called builder
		StringBuilder builder = new StringBuilder();
		// use the method StringBuilder::append to append string to builder.
		builder.append("<table style=\"margin: auto;width: 90%;\" border=\"1\">");
		builder.append("<caption>Test Table</caption>");
		builder.append("<tr style=\"height: 2rem;\">");
		
		for(List row :data) {
			builder.append("<tr>");
			for(List<String> value : data) {
				builder.append("<td>value</td>");
			}
			builder.append("</tr>");
		}
		
		// use the method StringBuilder::toString to get the final string out of builder.
		// using the string builder and the argument fill in the sample html table below:
				
		// for each name in COLUMN_NAMES
		// append <th>name</th>
		// end loop
		// "</tr>"
		// "<tr>"
		// for each row in data
		// "<tr>"
		// for each value in row
		// append <td>value</td>
		// end loop
		// "</tr>"
		// end loop
				
				
		// "</tr>"
		// "</table>"
		builder.append("</tr>");
		builder.append("</table>");
				webView.getEngine().loadContent(builder.toString());

		// TODO webView.getEngine().loadContent( builder.toString());
		// extract the string out of builder and load it in the webview engine.
	}

	/**
	 * Search method. This method is called after the connection to the DB is established.
	 * 
	 * @param searchTerm - a nullable string which represent what text to search on DB, empty or null mean get
	 *                   everything.
	 * @return a two dimensional list of data retrieved from DB. This list will never be null but it can be empty.
	 * @throws SQLException this exception is if there is an issue with DB access, connection is closed or any
	 *                      exceptions forwarded from {@link Connection#prepareStatement(String)} or
	 *                      {@link PreparedStatement#executeQuery()}.
	 */
	private ObservableList< List< String>> search( String searchTerm) throws SQLException {
		// TODO create a new ObservableList< List< String>> called list and initialize it using FXCollections.observableArrayList().
		// TODO add two rows of data to list using add method.
		// TODO for each row of data use List.of() method, ex. List.of( "1", "a", "b", "c").
		// TODO return the list.
		ObservableList<List <String>> list;
		list = FXCollections.observableArrayList();
		list.add("1");
		List.of("1", "administrator", "admin", "admin");
		return list
				;
	}

	private void connectTo( String dbURL, String user, String pass) throws SQLException {
		//skip the body of this method
	}

	/**
	 * main starting point of the application
	 * 
	 * @param args - arguments provided through command line, if any
	 */
	public static void main( String[] args) {
		// launch( args); method starts the javaFX application.
		// some IDEs are capable of starting JavaFX without this method.
		launch( args);
	}
}
