package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepartmentDaoJDBC implements DepartmentDao {

    private Connection connection;

    public DepartmentDaoJDBC(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Department department) {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(
                    "INSERT INTO department " +
                            "(Name) " +
                            "VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            st.setString(1, department.getName());

            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    department.setId(id);
                }
                DB.closeResultSet(rs);
            } else {
                throw new DbException("Unexpected error! No rows affected!");
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void update(Department department) {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(
                    "UPDATE department " +
                            "SET Name = ? WHERE Id = ?"
            );

            st.setString(1, department.getName());
            st.setInt(2, department.getId());

            st.executeUpdate();

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("DELETE FROM department WHERE Id = ?");
            st.setInt(1, id);

            int rowsAffected = st.executeUpdate();

            if (rowsAffected == 0) {
                throw new DbException("This id does not exist");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public Department findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(
                    "SELECT * FROM department WHERE ID = ?"
            );
            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                Department department = new Department();
                department.setId(rs.getInt(1));
                department.setName(rs.getString(2));

                return department;
            }
            return null;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Department> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = connection.prepareStatement(
                    "SELECT * FROM department"
            );
            rs = st.executeQuery();

            List<Department> list = new ArrayList<>();

            while (rs.next()) {
                Department department = new Department();
                department.setId(rs.getInt(1));
                department.setName(rs.getString(2));
                list.add(department);
            }

            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

}
