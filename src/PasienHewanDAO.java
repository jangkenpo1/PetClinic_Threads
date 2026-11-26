import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PasienHewanDAO {

    public void insert(PasienHewan p) throws SQLException {
        String sql = "INSERT INTO pasien_hewan (nama_hewan, jenis_hewan, keluhan) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, p.getNamaHewan());
                ps.setString(2, p.getJenisHewan());
                ps.setString(3, p.getKeluhan());
                ps.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public List<PasienHewan> findAll() throws SQLException {
        List<PasienHewan> list = new ArrayList<>();
        String sql = "SELECT id, nama_hewan, jenis_hewan, keluhan FROM pasien_hewan ORDER BY id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new PasienHewan(
                        rs.getInt("id"),
                        rs.getString("nama_hewan"),
                        rs.getString("jenis_hewan"),
                        rs.getString("keluhan")
                ));
            }
        }
        return list;
    }

    public boolean update(PasienHewan p) throws SQLException {
        String sql = "UPDATE pasien_hewan SET nama_hewan = ?, jenis_hewan = ?, keluhan = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, p.getNamaHewan());
                ps.setString(2, p.getJenisHewan());
                ps.setString(3, p.getKeluhan());
                ps.setInt(4, p.getId());

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    conn.commit();
                    return true;
                }
                conn.rollback();
                return false;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM pasien_hewan WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    conn.commit();
                    return true;
                }
                conn.rollback();
                return false;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}
