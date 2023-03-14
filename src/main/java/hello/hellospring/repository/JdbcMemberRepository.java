package hello.hellospring.repository;

import hello.hellospring.domain.Member;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Optional;

public class JdbcMemberRepository  implements MemberRepository{

    private final DataSource dataSource;

    public JdbcMemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Override
    public Member save(Member member) {
        String sql = "insert into member(name) values(?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);  // RETURN_GENERATED_KEYS: id(1,2,3)입력하지 않아도 자동입력시킴

            pstmt.setString(1, member.getName());  // parameterindex:1 = sql의 첫번째 ?

            pstmt.executeUpdate();  // 실제 쿼리 날라감
            rs = pstmt.getGeneratedKeys();  // RETURN_GENERATED_KEYS이거랑 매칭되어서 할 수 있는 것

            if (rs.next()) {
                member.setId(rs.getLong(1));
            } else {
                throw new SQLException("id 조회 실패");
            }
            return member;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);  // 리소스 반환필수!
        }
    }

    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    private void close(Connection conn, PreparedStatement pstmt, ResultSet rs)
    {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) {
                close(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void close(Connection conn) throws SQLException {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }
}