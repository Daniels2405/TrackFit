/**
 * TrackFit - Scripts de interfaz y utilidades de usuario
 * SC-403 Desarrollo de Aplicaciones Web y Patrones
 *
 * Todo el JavaScript propio vive aquí (igual que rutinas.js en el caso de estudio).
 * Las vistas NO llevan <script> en línea: los textos traducidos llegan por atributos data-*.
 */
document.addEventListener('DOMContentLoaded', function () {

    // ------------------------------------------------------------------
    // Utilidad: lee un atributo data-* de un botón (cadena vacía si no existe)
    // ------------------------------------------------------------------
    function attr(boton, nombre) {
        return boton.getAttribute(nombre) || '';
    }

    // ------------------------------------------------------------------
    // 1. Campo "Especialidad": solo visible cuando se marca el rol ENTRENADOR
    // ------------------------------------------------------------------
    const checkboxEntrenador = document.querySelector('input[type="checkbox"][data-rol="ENTRENADOR"]');
    const divEspecialidad = document.getElementById('divEspecialidad');
    const inputEspecialidad = document.getElementById('usuarioEspecialidad');

    function actualizarEspecialidad(limpiar) {
        if (!checkboxEntrenador || !divEspecialidad) {
            return;
        }
        if (checkboxEntrenador.checked) {
            divEspecialidad.classList.remove('d-none');
        } else {
            divEspecialidad.classList.add('d-none');
            if (limpiar && inputEspecialidad) {
                inputEspecialidad.value = '';
            }
        }
    }

    if (checkboxEntrenador) {
        checkboxEntrenador.addEventListener('change', function () {
            actualizarEspecialidad(true);
        });
        actualizarEspecialidad(false);
    }

    // ------------------------------------------------------------------
    // 2. Modal genérico de eliminación (#confirmModal)
    //    El botón que lo abre define: data-bs-id, data-bs-descripcion,
    //    data-bs-action (URL) y data-bs-param (nombre del parámetro: idPlan, cedula, idMembresia...)
    // ------------------------------------------------------------------
    const confirmModal = document.getElementById('confirmModal');
    if (confirmModal) {
        confirmModal.addEventListener('show.bs.modal', function (event) {
            const boton = event.relatedTarget;
            if (!boton) {
                return;
            }
            const inputId = document.getElementById('modalId');
            const descripcion = document.getElementById('modalDescripcion');
            const formulario = document.getElementById('confirmForm');

            if (inputId) {
                inputId.name = attr(boton, 'data-bs-param') || 'id';
                inputId.value = attr(boton, 'data-bs-id');
            }
            if (descripcion) {
                descripcion.textContent = attr(boton, 'data-bs-descripcion');
            }
            if (formulario && boton.getAttribute('data-bs-action')) {
                formulario.setAttribute('action', boton.getAttribute('data-bs-action'));
            }
        });
    }

    // ------------------------------------------------------------------
    // 3. Formularios que piden confirmación (atributo data-confirmar con el texto ya traducido)
    // ------------------------------------------------------------------
    document.querySelectorAll('form[data-confirmar]').forEach(function (form) {
        form.addEventListener('submit', function (event) {
            if (!window.confirm(form.getAttribute('data-confirmar'))) {
                event.preventDefault();
            }
        });
    });

    // ------------------------------------------------------------------
    // 4. Modal de PLAN: modo agregar / editar
    // ------------------------------------------------------------------
    const planModal = document.getElementById('planModal');
    if (planModal) {
        planModal.addEventListener('show.bs.modal', function (event) {
            const boton = event.relatedTarget;
            const editar = boton && boton.getAttribute('data-modo') === 'editar';
            document.getElementById('planForm').reset();

            document.getElementById('planId').value = editar ? attr(boton, 'data-id') : '';
            document.getElementById('planModalTitulo').textContent =
                editar ? planModal.dataset.tituloEditar : planModal.dataset.tituloAgregar;

            if (editar) {
                document.getElementById('planNombre').value = attr(boton, 'data-nombre');
                document.getElementById('planPrecio').value = attr(boton, 'data-precio');
                document.getElementById('planDuracion').value = attr(boton, 'data-duracion');
                document.getElementById('planAnticipacion').value = attr(boton, 'data-anticipacion');
                document.getElementById('planBeneficiosTexto').value = attr(boton, 'data-beneficios');
                document.getElementById('planActivo').checked = attr(boton, 'data-activo') === 'true';
            } else {
                document.getElementById('planActivo').checked = true;
            }
        });
    }

    // ------------------------------------------------------------------
    // 5. Modal de CLIENTE: modo agregar / editar
    // ------------------------------------------------------------------
    const clienteModal = document.getElementById('clienteModal');
    if (clienteModal) {
        clienteModal.addEventListener('show.bs.modal', function (event) {
            const boton = event.relatedTarget;
            const editar = boton && boton.getAttribute('data-modo') === 'editar';
            document.getElementById('clienteForm').reset();

            const cedula = document.getElementById('clienteCedula');
            const password = document.getElementById('clientePassword');
            const ayudaPassword = document.getElementById('clientePasswordHelp');

            document.getElementById('clienteModoEdicion').value = editar ? 'true' : 'false';
            document.getElementById('clienteModalTitulo').textContent =
                editar ? clienteModal.dataset.tituloEditar : clienteModal.dataset.tituloAgregar;
            cedula.readOnly = editar;
            password.required = !editar;
            if (ayudaPassword) {
                ayudaPassword.classList.toggle('d-none', !editar);
            }

            if (editar) {
                cedula.value = attr(boton, 'data-cedula');
                document.getElementById('clienteUsername').value = attr(boton, 'data-username');
                document.getElementById('clienteNombre').value = attr(boton, 'data-nombre');
                document.getElementById('clienteApellido1').value = attr(boton, 'data-apellido1');
                document.getElementById('clienteApellido2').value = attr(boton, 'data-apellido2');
                document.getElementById('clienteCorreo').value = attr(boton, 'data-correo');
                document.getElementById('clienteTelefono').value = attr(boton, 'data-telefono');
                document.getElementById('clienteActivo').checked = attr(boton, 'data-activo') === 'true';
            } else {
                cedula.value = '';
                document.getElementById('clienteActivo').checked = true;
            }
        });
    }

    // ------------------------------------------------------------------
    // 6. Modal de USUARIO: modo agregar / editar (incluye roles y especialidad)
    // ------------------------------------------------------------------
    const usuarioModal = document.getElementById('usuarioModal');
    if (usuarioModal) {
        usuarioModal.addEventListener('show.bs.modal', function (event) {
            const boton = event.relatedTarget;
            const editar = boton && boton.getAttribute('data-modo') === 'editar';
            document.getElementById('usuarioForm').reset();
            document.querySelectorAll('.check-rol').forEach(function (chk) {
                chk.checked = false;
            });

            const cedula = document.getElementById('usuarioCedula');
            const password = document.getElementById('usuarioPassword');
            const ayudaPassword = document.getElementById('usuarioPasswordHelp');

            document.getElementById('usuarioModoEdicion').value = editar ? 'true' : 'false';
            document.getElementById('usuarioModalTitulo').textContent =
                editar ? usuarioModal.dataset.tituloEditar : usuarioModal.dataset.tituloAgregar;
            cedula.readOnly = editar;
            password.required = !editar;
            if (ayudaPassword) {
                ayudaPassword.classList.toggle('d-none', !editar);
            }

            if (editar) {
                cedula.value = attr(boton, 'data-cedula');
                document.getElementById('usuarioUsername').value = attr(boton, 'data-username');
                document.getElementById('usuarioNombre').value = attr(boton, 'data-nombre');
                document.getElementById('usuarioApellido1').value = attr(boton, 'data-apellido1');
                document.getElementById('usuarioApellido2').value = attr(boton, 'data-apellido2');
                document.getElementById('usuarioCorreo').value = attr(boton, 'data-correo');
                document.getElementById('usuarioTelefono').value = attr(boton, 'data-telefono');
                document.getElementById('usuarioEspecialidad').value = attr(boton, 'data-especialidad');
                document.getElementById('usuarioActivo').checked = attr(boton, 'data-activo') === 'true';

                attr(boton, 'data-roles').split(',').forEach(function (idRol) {
                    const chk = document.getElementById('rol_' + idRol.trim());
                    if (chk) {
                        chk.checked = true;
                    }
                });
            } else {
                cedula.value = '';
                document.getElementById('usuarioActivo').checked = true;
            }

            actualizarEspecialidad(false);
        });
    }

    // ------------------------------------------------------------------
    // 8. Alertas de validación del navegador en el idioma de TrackFit
    //    ("Completa este campo", "Selecciona un elemento de la lista"...).
    //    El navegador las muestra en su propio idioma; aquí se reemplazan por
    //    los textos de messages.properties que llegan en <meta name="tf-validacion">.
    // ------------------------------------------------------------------
    const metaValidacion = document.querySelector('meta[name="tf-validacion"]');
    if (metaValidacion) {
        const texto = function (nombre, valor) {
            return attr(metaValidacion, 'data-' + nombre).replace('{0}', valor === undefined ? '' : valor);
        };

        const mensajeValidacion = function (campo) {
            const v = campo.validity;
            if (v.valueMissing) {
                if (campo.tagName === 'SELECT') {
                    return texto('seleccione');
                }
                if (campo.type === 'checkbox' || campo.type === 'radio') {
                    return texto('casilla');
                }
                return texto('requerido');
            }
            if (v.rangeUnderflow) {
                return texto('minimo', campo.min);
            }
            if (v.rangeOverflow) {
                return texto('maximo', campo.max);
            }
            if (v.stepMismatch) {
                return texto('paso');
            }
            if (v.typeMismatch && campo.type === 'email') {
                return texto('correo');
            }
            if (v.tooShort) {
                return texto('largo-minimo', campo.minLength);
            }
            if (v.tooLong) {
                return texto('largo-maximo', campo.maxLength);
            }
            if (v.typeMismatch || v.patternMismatch || v.badInput) {
                return texto('formato');
            }
            return '';
        };

        // "invalid" no burbujea: se escucha en fase de captura para todos los formularios
        document.addEventListener('invalid', function (event) {
            const campo = event.target;
            campo.setCustomValidity('');
            if (!campo.validity.valid) {
                campo.setCustomValidity(mensajeValidacion(campo));
            }
        }, true);

        // Al corregir el campo se limpia el mensaje para que el navegador lo vuelva a evaluar
        const limpiarValidacion = function (event) {
            if (event.target.setCustomValidity) {
                event.target.setCustomValidity('');
            }
        };
        document.addEventListener('input', limpiarValidacion, true);
        document.addEventListener('change', limpiarValidacion, true);
    }

    // ------------------------------------------------------------------
    // 7. Ocultar automáticamente los Toasts tras 4 segundos
    // ------------------------------------------------------------------
    setTimeout(function () {
        document.querySelectorAll('.toast').forEach(function (toast) {
            toast.classList.remove('show');
        });
    }, 4000);
});
