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
@WebServlet(name = "BorrarSociosServlet", value = "/BorrarSociosServlet")
public class BorrarSociosServlet extends HttpServlet {

    //EL SERVLET TIENE INSTANCIADO EL DAO PARA ACCESO A BBDD A LA TABLA SOCIO
    //                                  |
    //                                  V
    private SocioDAO socioDAO = new SocioDAOImpl();

    //HTML5 SÓLO SOPORTA GET Y POST
    //FRENTE A API REST UTLIZANDO CÓDIGO DE CLIENTE JS HTTP: GET, POST, PUT, DELETE, PATCH


    //MÉTODO PARA RUTAS POST /BorrarSociosServlet
    //PARA LA RUTA POST /BorrarSociosServlet HAY 2 OPCIONES DE REDIRECCIÓN INTERNA A JSP
    //1a CASO DE QUE SE VALIDE CORRECTAMENTE --> listadoSociosB.jsp   con el elemento eliminado del listado
    //2o CASO DE QUE NO SE VALIDE CORRECTAMENTE --> listadoSociosB.jsp CON INFORME DE ERROR  Y listado sin eliminar el elemento
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        RequestDispatcher dispatcher = null;

        //CÓDIGO DE VALIDACIÓN ENCAPSULADO EN UN MÉTODO DE UTILERÍA
        // SI OK ==> OPTIONAL CON SOCIO                 |
        // SI FAIL ==> EMPTY OPTIONAL                   |
        //                                              V
        Optional<Integer> optionalSocio = UtilServlet.validaBorrar(request);

        //SI OPTIONAL CON SOCIO PRESENTE <--> VALIDA OK
        if (optionalSocio.isPresent()) {

            //ACCEDO AL VALOR DE OPTIONAL DE INTEGER
            Integer idSocio = optionalSocio.get();

            //PERSISTO EL SOCIO NUEVO EN BBDD DELETE
            this.socioDAO.delete(idSocio);

            //CARGO TODO EL LISTADO DE SOCIOS DE BBDD CON EL NUEVO
            List<Socio> listado = this.socioDAO.getAll();

            //PREPARO ATRIBUTO EN EL ÁMBITO DE REQUEST PARA PASAR A JSP EL LISTADO
            //A RENDERIZAR. UTILIZO EL ÁMBITO DEL REQUEST DADO QUE EN EL FORWARD A
            //LA JSP SIGUE "VIVO" Y NO NECESITO ACCEDER AL ÁMBITO DE SESIÓN QUE REQUERIRÍA
            //DE UN CONTROL DE BORRADO DEL ATRIBUTO DESPUÉS DE SU USO.
            //EN request HAY UN Map<String, Object> DONDE PREPARO EL ATRIBUTO PARA LA VISTA JSP
            //                                  |
            //                                  V
            request.setAttribute("listado", listado);

            //POR ÚLTIMO, REDIRECCIÓN INTERNA PARA LA URL /BorrarSociosServlet A listadoSociosB.jsp
            //                                                                      |
            //                                                                      V
            dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/listadoSociosB.jsp");

        } else {

            //El OPTIONAL ESTÁ VACÍO (EMPTY)
            //PREPARO MENSAJE DE ERROR EN EL ÁMBITO DEL REQUEST PARA LA VISTA JSP
            //                                |
            //                                V
            request.setAttribute("error", "Error de validación!");

            //POR ÚLTIMO, REDIRECCIÓN INTERNA PARA LA URL /BorrarSociosServlet A listadoSociosB.jsp
            //                                                                      |
            //                                                                      V
            dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/listadoSociosB.jsp");
        }
        //TENEMOS SIEMPRE QUE HACER FORWARD CON LOS OBJETOS request Y response
        dispatcher.forward(request,response);

    }

}