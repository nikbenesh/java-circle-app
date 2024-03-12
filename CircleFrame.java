// Эта программа выводит на экран окружность с тремя случайными точками на ней.
// Затем она соединяет эти точки, создав треугольник.
// Точки можно перемещать по окружности.
// При их перемещении программа перерисует треугольник динамически.

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.awt.geom.*;

class Geom extends JPanel {
	private Ellipse2D current;
	private Path2D path;
	private Ellipse2D[] points;
	private int radius;
	private int x0 = 100;
	private int y0 = 10;

	private void setCurrent(Ellipse2D p) {
		current = p;
	}

	public Geom () {
		points = new Ellipse2D[3];
		double x[] = new double[3];
		double y[] = new double[3];
		radius = 200;
		x0 = 100;
		y0 = 10;
		for (int i=0; i < 3; i++) {
				double angle = Math.random() * 2*Math.PI;
				x[i] = (Math.cos(angle) * radius + radius) + x0;
				y[i] = (radius - Math.sin(angle) * radius) + y0;
				points[i] = new Ellipse2D.Double(x[i]-3, y[i]-3, 10, 10);
		}

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				int ex = event.getX();
				int ey = event.getY();
				for (Ellipse2D point : points) {
					if (point.contains(ex, ey))
						setCurrent(point);
				}
				System.out.println("Нажатие по точке " + current);
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent event) {
				if (current != null) {
					current.setFrame(event.getX(), event.getY(), 10, 10);
					repaint();
				}
			}
		});
	}

	// Этот метод находит точку пересечения окружности и прямой, 
	// проведенной из центра данной окружности к данной точке
	private Point2D findIntersectionPoint(Ellipse2D circle, Point2D p) {
		int x = (int)p.getX();
		int y = (int)p.getY();

		double radius = circle.getWidth()/2;

		int cx = (int)circle.getCenterX();
		int cy = (int)circle.getCenterY();

		int dx = x - cx;
		int dy = cy - y;

		if (dx == 0)
			dx = 1;
		if (dy == 0)
			dy = 1;

		double hypotenuse = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		double angle_cos = Math.abs(dx)/hypotenuse;
		double rx = dx/Math.abs(dx) * angle_cos*radius + radius;
		double ry = -dy/Math.abs(dy) * Math.sqrt(1 - Math.pow(angle_cos, 2))*radius + radius;

		return new Point2D.Double(rx, ry);
	}

	public void paintComponent(Graphics gr) {
		Graphics2D g = (Graphics2D) gr;
		Ellipse2D circle = new Ellipse2D.Double(x0, y0, 2*radius, 2*radius);

		for (Ellipse2D point : points) {
			if (!circle.intersects(point.getCenterX(), point.getCenterY(), 10, 10) ||
			 circle.contains(point.getCenterX(), point.getCenterY(), 10, 10)) {
				Point2D p = findIntersectionPoint(circle, new Point2D.Double(point.getCenterX(), point.getCenterY()));
				if (p != null)
					point.setFrame(p.getX() + x0, p.getY() + y0, 10, 10);
			}
		}

		path = new Path2D.Double();
		for (int i=0; i < 3; i++) {
			if (i == 0)
				path.moveTo(points[0].getCenterX(), points[0].getCenterY());
			else
				path.lineTo(points[i].getCenterX(), points[i].getCenterY());
		}
		path.closePath();

		g.draw(circle);
		g.draw(path);
		g.setPaint(Color.RED);
		for (Ellipse2D point : points)
			g.fill(point);
	}

	public Dimension getPreferredSize() {
		return new Dimension(400, 400);
	}
}

public class CircleFrame extends JFrame {
	public CircleFrame() {
		setTitle("Desktop App");
		setSize(600, 600);

		JPanel panel = new Geom();
		add(panel);
		panel.setLocation(0, 0);
	}

	public static void main(String[] args) {
		System.out.println("Примечание: перед тем, как оттянуть точку (передвинуть ее по окружности),\n нажмите мышкой по ее центру");
		EventQueue.invokeLater(() -> {
			JFrame frame = new CircleFrame();
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.setLocationByPlatform(true);
			frame.setVisible(true);
		});
	}
}