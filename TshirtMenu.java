package tshirtcp;

import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.imageio.ImageIO;
import java.util.ArrayList;



class CartItem {
    private String name;
    private double price;
    private Tshirt tshirt;
    private String size;
    private int quantity;
    private String color;

    public CartItem(String name, double price, Tshirt tshirt, String size, int quantity, String color) {
        this.name = name;
        this.price = price;
        this.tshirt = tshirt;
        this.size = size;
        this.quantity = quantity;
        this.color = color; 
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public Tshirt getTshirt() {
        return tshirt;
    }

    public String getSize() {
        return size;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getColor() {
        return color;
    }
}



class Tshirt {
    private int tshirtID;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String brand;
    
    public Tshirt(int tshirtID, String name, String description, double price, String imageUrl, String brand) {
        this.tshirtID = tshirtID;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.brand = brand;
    }

    public int getTshirtID() {
        return tshirtID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }	
    public String getBrand() {
        return brand;
    }
}




public class TshirtMenu extends JFrame {
    private Connection conn;
    private ArrayList<Tshirt> tshirts = new ArrayList<>();
    private ArrayList<CartItem> cart = new ArrayList<>();
    private JComboBox<String> brandSelector;
    private JPanel filterPanel;
    
    
    	
    public TshirtMenu() {
        setTitle("Tshirt Menu");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeDatabase();
        getContentPane().setBackground(Color.WHITE);

        JPanel tshirtsPanel = new	 JPanel();
        tshirtsPanel.setLayout(new BoxLayout(tshirtsPanel, BoxLayout.Y_AXIS));
        
     

        loadTshirtsFromDatabase();
        for (Tshirt tshirt : tshirts) {
            JPanel tshirtPanel = createTshirtPanel(tshirt);
            tshirtsPanel.add(tshirtPanel);
            
            
        }

        JScrollPane scrollPane = new JScrollPane(tshirtsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        getContentPane().add(scrollPane);
        
        
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(Color.GREEN); 
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        brandSelector = new JComboBox<>(new String[]{"All", "Adidas", "Levis", "H&M"});
        brandSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedBrand = (String) brandSelector.getSelectedItem();
                filterTshirtsByBrand(selectedBrand);
                refreshTshirtDisplay();
            }
        });
        filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.BLUE); 
        JLabel brandLabel = new JLabel("Filter by Brand:");
        filterPanel.add(brandLabel);
        filterPanel.add(brandSelector);
        getContentPane().add(filterPanel, BorderLayout.NORTH);
        
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.add(searchPanel, BorderLayout.NORTH);
        containerPanel.add(filterPanel, BorderLayout.CENTER);

        // Add the container panel to the frame
        getContentPane().add(containerPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane);

        
        
        
        
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String searchQuery = searchField.getText();
                performSearch(searchQuery);
            }
        });
        
        
        
       

        setVisible(true);
    }

    private void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tscp", "root", "sqlbawater@12");
        } catch (Exception e) {
            System.out.println(e);
        }
    
    
   
  
}
    private void loadTshirtsFromDatabase() {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tshirts");
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int tshirtID = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                double price = resultSet.getDouble("price");
                String imageUrl = resultSet.getString("imageUrl");
                String brand = resultSet.getString("brand");
                Tshirt tshirt = new Tshirt(tshirtID, name, description, price, imageUrl, brand);
                tshirts.add(tshirt);
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private JPanel createTshirtPanel(Tshirt tshirt) {	
        JPanel tshirtPanel = new JPanel(new BorderLayout());
        
        ImageIcon icon = loadImageIcon(tshirt.getImageUrl(), 400, 400);
        JLabel tshirtImageLabel = new JLabel(icon);

        JPanel tshirtInfoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("<html><b>Name:</b></html>");
        JLabel nameValue = new JLabel(tshirt.getName());
        JLabel brandLabel = new JLabel("<html><b>Brand:</b></html>");
        JLabel brandValue = new JLabel(tshirt.getBrand());  // Get the brand from the Tshirt object
     

        JLabel descriptionLabel = new JLabel("<html><b>Description:</b></html>");
        JTextArea descriptionValue = new JTextArea(tshirt.getDescription());
        descriptionValue.setLineWrap(true);
        descriptionValue.setWrapStyleWord(true);
        descriptionValue.setEditable(false);
        descriptionValue.setPreferredSize(new Dimension(200, 100));
        descriptionValue.setBackground(Color.ORANGE);

        JLabel priceLabel = new JLabel("<html><b>Price:</b></html>");
        JLabel priceValue = new JLabel("₹" + tshirt.getPrice());

        JLabel colorTitleLabel = new JLabel("<html><b>Select Color:</b></html>");
        JComboBox<String> colorSelector = new JComboBox<>(new String[]{"Red", "Blue", "Yellow", "Green"});

        JLabel sizeTitleLabel = new JLabel("<html><b>Select Size:</b></html>");
        JComboBox<String> sizeSelector = new JComboBox<>(new String[]{"S", "M", "L", "XL"});

        JLabel quantityTitleLabel = new JLabel("<html><b>Select Quantity:</b></html>");
        JComboBox<Integer> quantitySelector = new JComboBox<>();
        for (int i = 1; i <= 10; i++) {
            quantitySelector.addItem(i);
        }

        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        tshirtInfoPanel.add(nameLabel, c);

        c.gridx = 1;
        tshirtInfoPanel.add(nameValue, c);

        c.gridy++;
        c.gridx = 0;
        tshirtInfoPanel.add(descriptionLabel, c);

        c.gridx = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        tshirtInfoPanel.add(descriptionValue, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        tshirtInfoPanel.add(priceLabel, c);	

        c.gridx = 1;
        tshirtInfoPanel.add(priceValue, c);

        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        tshirtInfoPanel.add(colorTitleLabel, c);

        c.gridx = 1;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        tshirtInfoPanel.add(colorSelector, c);

        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        tshirtInfoPanel.add(sizeTitleLabel, c);

        c.gridx = 1;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        tshirtInfoPanel.add(sizeSelector, c);

        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        tshirtInfoPanel.add(quantityTitleLabel, c);

        c.gridx = 1;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        tshirtInfoPanel.add(quantitySelector, c);
        
        c.gridy++;
        c.gridx = 0;
        tshirtInfoPanel.add(brandLabel, c);
        
        c.gridx = 1;
        tshirtInfoPanel.add(brandValue, c);
        
        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                // Insert the item into the cart table
                insertCartItemToDatabase(tshirt, sizeSelector, quantitySelector, colorSelector);
            }
        });

        
        JButton viewCartButton = new JButton("View Cart");
        viewCartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Open a new window to display the cart items
                displayCart();
            }
        });
        

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addToCartButton);
        buttonPanel.add(viewCartButton);
        buttonPanel.setBackground(Color.GREEN); 

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);


        tshirtPanel.add(tshirtImageLabel, BorderLayout.WEST);
        tshirtPanel.add(tshirtInfoPanel, BorderLayout.CENTER);
        tshirtPanel.add(addToCartButton, BorderLayout.SOUTH);
        tshirtPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        return tshirtPanel;
    }

    private ImageIcon loadImageIcon(String imageUrl, int width, int height) {
        try {
            URL url = new URL(imageUrl);
            Image image = ImageIO.read(url);

            if (image == null) {
                System.err.println("Error loading image: " + imageUrl);
                return new ImageIcon();
            }

            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            e.printStackTrace();  // Print the exception details for debugging
        }

        return new ImageIcon();
    }
    
  
    
    // Insert a cart item into the database
    private void insertCartItemToDatabase(Tshirt tshirt, JComboBox<String> sizeSelector, JComboBox<Integer> quantitySelector, JComboBox<String> colorSelector) {
        try {
        	 String selectedColor = (String) colorSelector.getSelectedItem();
            String selectedSize = (String) sizeSelector.getSelectedItem();
            int selectedQuantity = (int) quantitySelector.getSelectedItem();
            // Retrieve the selected color

            // Create a new CartItem and add it to the cart list
            CartItem cartItem = new CartItem(tshirt.getName(), tshirt.getPrice(), tshirt, selectedSize, selectedQuantity, selectedColor);
            cart.add(cartItem);

            PreparedStatement stmt = conn.prepareStatement("INSERT INTO cart (tshirt_id, size, quantity, color) VALUES (?, ?, ?, ?)");
            stmt.setInt(1, tshirt.getTshirtID());
            stmt.setString(2, selectedSize);
            stmt.setInt(3, selectedQuantity);
            stmt.setString(4, selectedColor); // Use the selectedColor variable here
            stmt.executeUpdate();

            stmt.close();
            System.out.println("Item added to the cart.");
        } catch (SQLException e) {
            System.out.println("Error adding item to the cart: " + e.getMessage());
        }
    }

    private void displayCart() {
        JFrame cartFrame = new JFrame("Cart");
        cartFrame.setSize(400, 400);
        cartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
        cartPanel.setBackground(Color.ORANGE);
       

        // Retrieve cart items from the database
        ArrayList<CartItem> cartItems = retrieveCartItemsFromDatabase();
        
        double totalCost = calculateTotalCost();
        JLabel totalCostLabel = new JLabel("Total Cost: ₹" + totalCost);
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.CYAN); 
        topPanel.add(totalCostLabel);
        cartFrame.getContentPane().add(topPanel, BorderLayout.NORTH);

        for (CartItem item : cartItems) {
            JPanel cartItemPanel = new JPanel(new BorderLayout());

            JLabel cartItemLabel = new JLabel(item.getName()+" - Price: ₹" + item.getPrice());


            cartItemPanel.add(cartItemLabel, BorderLayout.CENTER);

            cartPanel.add(cartItemPanel);
        }

        JScrollPane cartScrollPane = new JScrollPane(cartPanel);
        cartFrame.getContentPane().add(cartScrollPane);
        
        

        JButton emptyCartButton = new JButton("Empty Cart");
        emptyCartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Empty the cart (remove all items from the database)
                emptyCart();
                cartPanel.removeAll(); // Remove all items from the cart display
                cartFrame.revalidate();
                cartFrame.repaint();
            }
        });

        cartFrame.getContentPane().add(emptyCartButton, BorderLayout.SOUTH);

        cartFrame.setVisible(true);
    }
    // Retrieve cart items from the database
    private ArrayList<CartItem> retrieveCartItemsFromDatabase() {
        ArrayList<CartItem> items = new ArrayList<>();
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT t.*, c.size, c.quantity FROM cart c JOIN tshirts t ON c.tshirt_id = t.id");
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int tshirtID = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                double price = resultSet.getDouble("price");
                String imageUrl = resultSet.getString("imageUrl");
                String brand = resultSet.getString("brand");
                String color = resultSet.getString("color");
                Tshirt tshirt = new Tshirt(tshirtID, name, description, price, imageUrl, brand);
                String size = resultSet.getString("size");
                int quantity = resultSet.getInt("quantity");
                items.add(new CartItem(name,  price, tshirt, size, quantity,color));
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return items;
    }

    
    
    
 // Remove a cart item from the database
    private void emptyCart() {
        try {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM cart");
            stmt.executeUpdate();
            stmt.close();

            // Clear the cart ArrayList and recalculate total cost
            cart.clear();
            double totalCost = calculateTotalCost();
            System.out.println("Cart emptied. Total Cost: ₹" + totalCost);
        } catch (SQLException e) {
            System.out.println("Error removing items from the cart: " + e.getMessage());
        }
    }

        
    
    
    
  

    private void filterTshirtsByBrand(String selectedBrand) {
        try {
            if ("All".equals(selectedBrand)) {
                loadTshirtsFromDatabase();
            } else {
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tshirts WHERE brand = ?");
                stmt.setString(1, selectedBrand);
                ResultSet resultSet = stmt.executeQuery();
                tshirts.clear();
                while (resultSet.next()) {
                    int tshirtID = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String description = resultSet.getString("description");
                    double price = resultSet.getDouble("price");
                    String imageUrl = resultSet.getString("imageUrl");
                    String brand = resultSet.getString("brand");
                    Tshirt tshirt = new Tshirt(tshirtID, name, description, price, imageUrl, brand);
                    tshirts.add(tshirt);
                }
                resultSet.close();
                stmt.close();
            }
            refreshTshirtDisplay();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }


    private void refreshTshirtDisplay() {
        // Remove existing T-shirt panels
        Container contentPane = getContentPane();
        contentPane.removeAll();

        // Rebuild the T-shirt panels
        JPanel tshirtsPanel = new JPanel();
        tshirtsPanel.setLayout(new BoxLayout(tshirtsPanel, BoxLayout.Y_AXIS));

        for (Tshirt tshirt : tshirts) {
            JPanel tshirtPanel = createTshirtPanel(tshirt);
            tshirtsPanel.add(tshirtPanel);
        }

        JScrollPane scrollPane = new JScrollPane(tshirtsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentPane.add(scrollPane);

     
        brandSelector = new JComboBox<>(new String[]{"All", "Adidas", "Levis", "H&M"});
        brandSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedBrand = (String) brandSelector.getSelectedItem();
                filterTshirtsByBrand(selectedBrand);
                refreshTshirtDisplay();
            }
        });
        filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel brandLabel = new JLabel("Filter by Brand:");
        filterPanel.add(brandLabel);
        filterPanel.add(brandSelector);
        getContentPane().add(filterPanel, BorderLayout.NORTH);

        setVisible(true);


        revalidate();
        repaint();
    }


public double calculateTotalCost() {
    double totalCost = 0.0;
    
    for (CartItem item : cart) {
        totalCost += item.getPrice() * item.getQuantity();
    }
    
    return totalCost;
}

private void performSearch(String searchQuery) {
    // Clear the existing tshirts list
    tshirts.clear();
    
    try {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tshirts WHERE name LIKE ? OR brand LIKE ?");
        stmt.setString(1, "%" + searchQuery + "%");
        stmt.setString(2, "%" + searchQuery + "%");
        ResultSet resultSet = stmt.executeQuery();
        while (resultSet.next()) {
            int tshirtID = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String description = resultSet.getString("description");
            double price = resultSet.getDouble("price");
            String imageUrl = resultSet.getString("imageUrl");
            String brand = resultSet.getString("brand");
            Tshirt tshirt = new Tshirt(tshirtID, name, description, price, imageUrl, brand);
            tshirts.add(tshirt);
        }
        resultSet.close();
        stmt.close();
        refreshTshirtDisplay();
    } catch (SQLException e) {
        System.out.println("Error performing search: " + e.getMessage());
    }
}

		
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TshirtMenu());
    }
}



