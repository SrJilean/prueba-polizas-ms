package com.seguros.polizas.service;

import com.seguros.polizas.dto.request.RiesgoReqDTO;
import com.seguros.polizas.entity.Actor;
import com.seguros.polizas.entity.HistoricoIPC;
import com.seguros.polizas.entity.HistoricoMovimiento;
import com.seguros.polizas.entity.Poliza;
import com.seguros.polizas.entity.Riesgo;
import com.seguros.polizas.exception.BusinessRuleException;
import com.seguros.polizas.exception.ResourceNotFoundException;
import com.seguros.polizas.repository.ActorRepository;
import com.seguros.polizas.repository.HistoricoIPCRepository;
import com.seguros.polizas.repository.HistoricoMovimientoRepository;
import com.seguros.polizas.repository.PolizaRepository;
import com.seguros.polizas.repository.RiesgoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PolizaService {

    private final PolizaRepository polizaRepository;
    private final RiesgoRepository riesgoRepository;
    private final HistoricoMovimientoRepository historicoMovimientoRepository;
    private final HistoricoIPCRepository historicoIPCRepository;
    private final CoreIntegrationService coreIntegrationService;
    private final ActorRepository actorRepository;

    // GET /polizas
    public List<Poliza> listarPolizas(String tipo, String estado) {
        if (tipo != null && estado != null) {
            return polizaRepository.findByTipoPolizaAndEstado(tipo, estado);
        } else if (tipo != null) {
            return polizaRepository.findByTipoPoliza(tipo);
        } else if (estado != null) {
            return polizaRepository.findByEstado(estado);
        }
        return polizaRepository.findAll();
    }

    // GET /polizas/{id}/riesgos
    public List<Riesgo> listarRiesgosPorPoliza(Long id) {
        Poliza poliza = polizaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada"));
        return poliza.getRiesgos();
    }

    @Transactional
    public Poliza renovarPoliza(Long id) {
        Poliza polizaActual = polizaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada"));

        if ("CANCELADA".equalsIgnoreCase(polizaActual.getEstado())) {
            throw new BusinessRuleException("No se puede renovar una póliza que se encuentra CANCELADA.");
        }

        int anioActual = java.time.LocalDate.now().getYear();
        HistoricoIPC ipcConfigurado = historicoIPCRepository.findTopByPeriodoOrderByIdDesc(anioActual)
                .orElseThrow(() -> new BusinessRuleException("No se encontró el IPC para el año " + anioActual));

        BigDecimal factorAumento = BigDecimal.ONE.add(ipcConfigurado.getValor());

        polizaActual.setEstado("RENOVADA");
        polizaActual.setIpcAplicado(ipcConfigurado.getValor());
        polizaRepository.save(polizaActual);

        Poliza nuevaPoliza = new Poliza();
        nuevaPoliza.setNumeroPoliza(polizaActual.getNumeroPoliza() + "-R");
        nuevaPoliza.setTipoPoliza(polizaActual.getTipoPoliza());
        nuevaPoliza.setEstado("ACTIVA");
        nuevaPoliza.setFechaInicio(java.time.LocalDate.now());
        nuevaPoliza.setVigenciaMeses(polizaActual.getVigenciaMeses());
        nuevaPoliza.setPolizaAnteriorId(polizaActual.getId());
        nuevaPoliza.setTomador(polizaActual.getTomador());
        
        nuevaPoliza.setValorCanonTotal(BigDecimal.ZERO);
        nuevaPoliza.setValorPrimaTotal(BigDecimal.ZERO);
        Poliza polizaGuardada = polizaRepository.save(nuevaPoliza);

        BigDecimal sumaNuevoCanon = BigDecimal.ZERO;
        BigDecimal sumaNuevaPrima = BigDecimal.ZERO;

        for (Riesgo riesgoViejo : polizaActual.getRiesgos()) {
            if ("ACTIVO".equalsIgnoreCase(riesgoViejo.getEstadoRiesgo())) {
                Riesgo riesgoNuevo = new Riesgo();
                riesgoNuevo.setDireccion(riesgoViejo.getDireccion());
                
                BigDecimal canonNuevoInd = riesgoViejo.getCanonIndividual().multiply(factorAumento);
                riesgoNuevo.setCanonIndividual(canonNuevoInd);
                
                BigDecimal primaNuevaInd = canonNuevoInd.multiply(new BigDecimal(polizaGuardada.getVigenciaMeses()));
                riesgoNuevo.setPrimaIndividual(primaNuevaInd);
                
                riesgoNuevo.setValorAsegurado(canonNuevoInd.multiply(new BigDecimal("36")));
                
                riesgoNuevo.setEstadoRiesgo("ACTIVO");
                riesgoNuevo.setAsegurado(riesgoViejo.getAsegurado());
                riesgoNuevo.setBeneficiario(riesgoViejo.getBeneficiario());
                riesgoNuevo.setPoliza(polizaGuardada);
                
                riesgoRepository.save(riesgoNuevo);
                
                sumaNuevoCanon = sumaNuevoCanon.add(canonNuevoInd);
                sumaNuevaPrima = sumaNuevaPrima.add(primaNuevaInd);
            }
        }

        polizaGuardada.setValorCanonTotal(sumaNuevoCanon);
        polizaGuardada.setValorPrimaTotal(sumaNuevaPrima);
        polizaRepository.save(polizaGuardada);

        BigDecimal canonAnterior = polizaActual.getValorCanonTotal() != null ? polizaActual.getValorCanonTotal() : BigDecimal.ZERO;
        BigDecimal primaAnterior = polizaActual.getValorPrimaTotal() != null ? polizaActual.getValorPrimaTotal() : BigDecimal.ZERO;

        HistoricoMovimiento movimiento = new HistoricoMovimiento();
        movimiento.setPoliza(polizaGuardada); // Dejamos el movimiento en la nueva póliza
        movimiento.setFechaMovimiento(java.time.LocalDateTime.now());
        movimiento.setValorCanonAnterior(canonAnterior);
        movimiento.setValorCanonNuevo(sumaNuevoCanon);
        movimiento.setValorPrimaAnterior(primaAnterior);
        movimiento.setValorPrimaNuevo(sumaNuevaPrima);
        movimiento.setTipoMovimiento("renovacion_automatica");
        movimiento.setIpcAplicado(ipcConfigurado.getValor());
        historicoMovimientoRepository.save(movimiento);

        coreIntegrationService.notificarActualizacionCore(polizaGuardada.getId());
        return polizaGuardada;
    }

    // POST /polizas/{id}/cancelar
    @Transactional
    public Poliza cancelarPoliza(Long id) {
        Poliza poliza = polizaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada"));

        poliza.setEstado("CANCELADA");

        // REGLA: La cancelación de una póliza cancela todos sus riesgos
        if (poliza.getRiesgos() != null) {
            poliza.getRiesgos().forEach(riesgo -> riesgo.setEstadoRiesgo("CANCELADO"));
        }

        Poliza guardada = polizaRepository.save(poliza);
        coreIntegrationService.notificarActualizacionCore(guardada.getId());
        return guardada;
    }

    @Transactional
    public Riesgo agregarRiesgoAPoliza(Long polizaId, RiesgoReqDTO dto) {
        Poliza poliza = polizaRepository.findById(polizaId)
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada"));

        if ("INDIVIDUAL".equalsIgnoreCase(poliza.getTipoPoliza())) {
            throw new BusinessRuleException("Una póliza INDIVIDUAL solo puede tener 1 riesgo.");
        }

        Actor asegurado = actorRepository.findById(dto.getAseguradoId())
                .orElseThrow(() -> new ResourceNotFoundException("Asegurado no encontrado"));
        Actor beneficiario = actorRepository.findById(dto.getBeneficiarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiario no encontrado"));

        BigDecimal canonAnterior = poliza.getValorCanonTotal() != null ? poliza.getValorCanonTotal() : BigDecimal.ZERO;
        BigDecimal primaAnterior = poliza.getValorPrimaTotal() != null ? poliza.getValorPrimaTotal() : BigDecimal.ZERO;

        // Calcular fecha fin y meses restantes
        java.time.LocalDate fechaFin = poliza.getFechaInicio().plusMonths(poliza.getVigenciaMeses());
        long mesesRestantes = ChronoUnit.MONTHS.between(java.time.LocalDate.now(), fechaFin);
        
        // Si la póliza ya está vencida, los meses restantes son 0
        if (mesesRestantes < 0) {
            mesesRestantes = 0;
        }

        Riesgo nuevoRiesgo = new Riesgo();
        nuevoRiesgo.setDireccion(dto.getDireccion());
        nuevoRiesgo.setCanonIndividual(dto.getCanonIndividual());
        
        // Multiplicación dinámica por los meses restantes
        BigDecimal primaCalculada = dto.getCanonIndividual()
                .multiply(new BigDecimal(mesesRestantes));
                
        nuevoRiesgo.setPrimaIndividual(primaCalculada);
        // El valor asegurado suele ser sobre un estándar de 36 meses, se mantiene fijo
        nuevoRiesgo.setValorAsegurado(dto.getCanonIndividual().multiply(new BigDecimal("36")));
        
        nuevoRiesgo.setAsegurado(asegurado);
        nuevoRiesgo.setBeneficiario(beneficiario);
        nuevoRiesgo.setPoliza(poliza);
        nuevoRiesgo.setEstadoRiesgo("ACTIVO");

        Riesgo riesgoGuardado = riesgoRepository.save(nuevoRiesgo);

        BigDecimal canonNuevo = canonAnterior.add(nuevoRiesgo.getCanonIndividual());
        BigDecimal primaNueva = primaAnterior.add(nuevoRiesgo.getPrimaIndividual());
        
        poliza.setValorCanonTotal(canonNuevo);
        poliza.setValorPrimaTotal(primaNueva);
        polizaRepository.save(poliza);

        // Registro exacto en el histórico de movimientos
        HistoricoMovimiento movimiento = new HistoricoMovimiento();
        movimiento.setPoliza(poliza); 
        movimiento.setFechaMovimiento(java.time.LocalDateTime.now());
        movimiento.setValorCanonAnterior(canonAnterior);
        movimiento.setValorCanonNuevo(canonNuevo);
        movimiento.setValorPrimaAnterior(primaAnterior);
        movimiento.setValorPrimaNuevo(primaNueva);
        movimiento.setTipoMovimiento("inclusion_riesgo"); 
        
        historicoMovimientoRepository.save(movimiento);

        coreIntegrationService.notificarActualizacionCore(poliza.getId());
        return riesgoGuardado;
    }

    /// POST /riesgos/{id}/cancelar
    @Transactional
    public Riesgo cancelarRiesgo(Long riesgoId) {
        Riesgo riesgo = riesgoRepository.findById(riesgoId)
                .orElseThrow(() -> new ResourceNotFoundException("Riesgo no encontrado"));

        if ("CANCELADO".equalsIgnoreCase(riesgo.getEstadoRiesgo())) {
            throw new BusinessRuleException("Este riesgo ya se encuentra cancelado.");
        }

        Poliza poliza = riesgo.getPoliza();

        // 1. Guardar valores anteriores para el histórico
        BigDecimal canonAnterior = poliza.getValorCanonTotal() != null ? poliza.getValorCanonTotal() : BigDecimal.ZERO;
        BigDecimal primaAnterior = poliza.getValorPrimaTotal() != null ? poliza.getValorPrimaTotal() : BigDecimal.ZERO;

        // 2. Actualizar estado del riesgo
        riesgo.setEstadoRiesgo("CANCELADO");
        Riesgo riesgoGuardado = riesgoRepository.save(riesgo);

        // 3. Restar los valores del riesgo a la póliza principal
        BigDecimal canonNuevo = canonAnterior.subtract(riesgo.getCanonIndividual());
        BigDecimal primaNueva = primaAnterior.subtract(riesgo.getPrimaIndividual());

        // Medida de seguridad matemática para evitar saldos negativos
        if (canonNuevo.compareTo(BigDecimal.ZERO) < 0) canonNuevo = BigDecimal.ZERO;
        if (primaNueva.compareTo(BigDecimal.ZERO) < 0) primaNueva = BigDecimal.ZERO;

        poliza.setValorCanonTotal(canonNuevo);
        poliza.setValorPrimaTotal(primaNueva);
        polizaRepository.save(poliza);

        // 4. Registro exacto en el histórico de movimientos
        HistoricoMovimiento movimiento = new HistoricoMovimiento();
        movimiento.setPoliza(poliza); 
        movimiento.setFechaMovimiento(java.time.LocalDateTime.now());
        movimiento.setValorCanonAnterior(canonAnterior);
        movimiento.setValorCanonNuevo(canonNuevo);
        movimiento.setValorPrimaAnterior(primaAnterior);
        movimiento.setValorPrimaNuevo(primaNueva);
        movimiento.setTipoMovimiento("exclusion_riesgo"); // Registrado según tu esquema
        
        historicoMovimientoRepository.save(movimiento);

        coreIntegrationService.notificarActualizacionCore(poliza.getId());
        return riesgoGuardado;
    }
}