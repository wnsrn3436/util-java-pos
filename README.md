# Java POS

[한국어](README.ko.md)

This is a Java Swing desktop POS program that manages a shop's table layout, orders and payment, products, members, and sales. Data is stored in MySQL through JDBC, and I drew the movable and resizable table widget, the sales graph, and the receipt barcode myself with Swing and AWT. It was a team project, and I was the team lead and the main programmer.

<p>
  <img src="docs/screenshots/screenshot-1.png" width="480" alt="Order tab">
</p>


## How to use

The archive downloaded from Releases contains the runnable jar and the database schema. To run it you need Java 8 or later, a MySQL server, and MySQL Connector/J.

1. Run a MySQL server on `localhost:3306` and set the password of the `root` account to `1234`. The connection settings are in `MainFrame.java`.
2. Run [docs/schema.sql](docs/schema.sql) to create the `pos` database and its tables. The schema was reconstructed from the queries in the code.
3. Download MySQL Connector/J and put it next to the jar. The driver class is `com.mysql.jdbc.Driver`, so it must be a 5.1 version (for example `mysql-connector-java-5.1.49.jar`). It is available on Maven Central as `mysql:mysql-connector-java`.
4. Put the connector on the classpath and run `MainFrame`. The classpath separator is `;` on Windows and `:` on macOS and Linux.

```
mysql -u root -p < schema.sql
java -cp "java-pos.jar;mysql-connector-java-5.1.49.jar" MainFrame
```

| Tab | Content |
|---|---|
| 주문관리 (orders) | Right-click an empty spot and choose "좌석추가" (add seat) to place a table. Clicking the edge of a table makes it follow the mouse, and clicking an empty spot drops it there. The small button at the top left resizes it the same way. Right-clicking the edge of a table deletes it. "테이블저장" (save tables) stores the layout in the database so it comes back the next time the program runs |
| 상품관리 (products) | Add a product, or look one up by product number and then edit or delete it |
| 회원관리 (members) | Add a member, or look one up by member number and then edit or delete it. A new member starts with 100 points and the grade "일반" (regular) |
| 매출관리 (sales) | Table of sales amounts by date and a line graph |

The "주문관리" button on a table opens the order window. Clicking a product in the product list on the right adds one to the order list and lowers the stock by one, and clicking a line in the order list removes one and returns it to stock. Each click updates the stock in the database right away. "계산" (checkout) records today's date and the total in the payment table `pay` and clears the table. After that, "영수증" (receipt) opens a receipt window with the order lines and a barcode. In the schema, `sales` is a view that sums `pay` by month, so payments show up in the sales tab as they accumulate.

The source is saved in UTF-8, so if you compile it yourself, use `javac -encoding UTF-8`.


## How it works

**I built the movable and resizable widget myself.** `Anchor` is a panel that wraps any component. It is 10 pixels larger than the wrapped component on every side and has a 10x10 button at the top left. Pressing the body switches it to move mode, where it follows the cursor on every `mouseMoved` of the parent panel. Pressing the top-left button switches it to resize mode, where the top-left corner moves with the cursor and the width and height change by the same amount. The widget sits 10 pixels to the lower right of the cursor, so the cursor stays over the parent panel and the parent keeps receiving the mouse events. Clicking the parent panel again ends the mode. The tables in the order tab (`PosTable`) and the graph in the sales tab (`Graph`) both sit inside this widget.

```java
parent.addMouseMotionListener(new MouseAdapter() {
    @Override
    public void mouseMoved(MouseEvent e) {
        if (Anchor.this.isBodyClicked) {
            Anchor.this.setLocation(e.getX() + 10, e.getY() + 10);
        }
        if (Anchor.this.isAnchorClicked)
        {
            int margin = 10;
            Point deltaLocation = new Point(e.getX() - Anchor.this.getX() + margin, e.getY() - Anchor.this.getY() + margin);
            Anchor.this.setDeltaSize(-deltaLocation.x , -deltaLocation.y);
            Anchor.this.setLocation(e.getX() + margin, e.getY() + margin);
        }
    }
});
```

**The table layout is rewritten in full.** "테이블저장" empties `table_info` and inserts one row per visible table with its size and position. A deleted table is only hidden, so it is left out of the save. On startup the program reads `table_info` and puts the tables back.

**The barcode is the time the receipt was opened, drawn in binary.** The month, day, hour, minute, and second are joined into one number, and `Barcode` turns it into a 32-bit binary string, appends `01`, and draws a vertical bar for every 1. The bar width is the canvas width divided by the number of digits.

```java
for (int i = binaryString.length() - 1, j = 0; i >= 0; i--, j++)
{
    if (binaryString.charAt(i) == '1')
    {
        g2d.draw(new Line2D.Float(barWidth * j, 0, barWidth * j, getHeight()));
    }
}
```

**The graph is normalized to its minimum and maximum.** `Graph` uses a separate `Canvas` for the horizontal axis, the vertical axis, and the body. For each value, the body places a point at a height set by where the value falls between the minimum and the maximum, then joins it to the previous point with a line. Opening the sales tab reads `sales` again each time, and the dates, with `-` removed, become the labels on the horizontal axis.


## Files

| Path | Content |
|---|---|
| `source/src/MainFrame.java` | Main window and tabs, database connection settings, saving and loading the table layout |
| `source/src/Anchor.java` | Movable and resizable widget |
| `source/src/PosTable.java` | Table in the order tab |
| `source/src/OrderFrame.java` | Order window, stock updates and payment |
| `source/src/ReceiptFrame.java`, `source/src/Barcode.java` | Receipt and barcode |
| `source/src/Graph.java` | Sales graph |
| `source/src/Item*.java`, `source/src/Member*.java` | Add, edit, and delete windows for products and members |
| `source/.project`, `source/.classpath` | Eclipse project files |
| `docs/schema.sql` | Schema of the `pos` database reconstructed from the queries in the code |
| `docs/screenshots/` | Screenshots |
| Releases | Runnable jar and database schema |


## License

zlib. See [LICENSE](LICENSE).
