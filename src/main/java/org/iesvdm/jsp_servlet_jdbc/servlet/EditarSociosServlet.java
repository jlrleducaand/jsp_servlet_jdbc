package org.iesvdm.jsp_servlet_jdbc.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.iesvdm.jsp_servlet_jdbc.dao.SocioDAO;
import org.iesvdm.jsp_servlet_jdbc.dao.SocioDAOImpl;
import org.iesvdm.jsp_servlet_jdbc.model.Socio;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

//PLANTILLA DE CÓDIGO PARA SERVLETs EN INTELLIJ
//https://www.jetbrains.com/help/idea/creating-and-configuring-web-application-elements.html

//1A APROX. PATRÓN MVC -> M(dao, model y bbdd), V(jsp) & C(servlet)

//                      v--NOMBRE DEL SERVLET           v--RUTAS QUE ATIENDE, PUEDE SER UN ARRAY {"/GrabarSociosServlet", "/grabar-socio"}
@WebServlet(name = "EditarSociosServlet", value = "/EditarSociosServlet")
public class EditarSociosServlet extends HttpServlet {


    private SocioDAO socioDAO = new SocioDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/formularioEditarSocioB.jsp");

        dispatcher.forward(request, response); // LA REDIRECCIÓN INTERNA EN EL SERVIDOR A UNA JSP O VISTA.

    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        RequestDispatcher dispatcher = null;

        Optional<Integer> optionalIdSocio = UtilServlet.validaEditar(request);

        //SI OPTIONAL CON SOCIO PRESENTE <--> VALIDA OK
        if (optionalIdSocio.isPresent()) {

            //ACCEDO AL VALOR DE OPTIONAL DE SOCIO
            Integer socioID = optionalIdSocio.get();

            //PERSITO EL SOCIO ACTUALIZADO EN BBDD
            this.socioDAO.update( new Socio (
                    Integer.parseInt(request.getParameter("codigo")),
                    request.getParameter("nombre"),
                    Integer.parseInt(request.getParameter("estatura")),
                    Integer.parseInt(request.getParameter("edad")),
                    request.getParameter("localidad")
            ));

            //CARGO TODO EL LISTADO DE SOCIOS DE BBDD CON EL NUEVO
            List<Socio> listado = this.socioDAO.getAll();

            request.setAttribute("listado", listado);

            //PARA LANZAR UN MODAL Y UN EFECTO SCROLL EN LA VISTA JSP
            request.setAttribute("newSocioID", socioID);

            dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/listadoSociosB.jsp");

        } else {

            request.setAttribute("error", "Error de validación!");

            dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/formularioEditarSocioB.jsp");
        }
        dispatcher.forward(request,response);
    }

}