import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PetClinicFrame frame = new PetClinicFrame();
            frame.setVisible(true);
        });
    }
}
