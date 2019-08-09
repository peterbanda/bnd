package com.bnd.network.domain;

import com.bnd.math.domain.rand.RandomDistribution;

import java.util.ArrayList;
import java.util.List;

/**
 * @author © Peter Banda
 * @since 2012
 */
public class TemplateTopology extends Topology {

    public static class IntraLayerEdgeSpec {
        private boolean allEdges;
        private Integer inEdgesNum;
        private RandomDistribution<Integer> inEdgesDistribution;
        private Integer edgesNum;
        private Double edgesRatio;
        private boolean allowMultiEdges;

        public boolean isAllEdges() {
            return allEdges;
        }

        public void setAllEdges(boolean allEdges) {
            this.allEdges = allEdges;
        }

        public Integer getInEdgesNum() {
            return inEdgesNum;
        }

        public void setInEdgesNum(Integer inEdgesNum) {
            this.inEdgesNum = inEdgesNum;
        }

        public RandomDistribution<Integer> getInEdgesDistribution() {
            return inEdgesDistribution;
        }

        public void setInEdgesDistribution(RandomDistribution<Integer> inEdgesDistribution) {
            this.inEdgesDistribution = inEdgesDistribution;
        }

        public Double getEdgesRatio() {
            return edgesRatio;
        }

        public void setEdgesRatio(Double edgesRatio) {
            this.edgesRatio = edgesRatio;
        }

        public Integer getEdgesNum() {
            return edgesNum;
        }

        public void setEdgesNum(Integer edgesNum) {
            this.edgesNum = edgesNum;
        }

        public boolean isAllowMultiEdges() {
            return allowMultiEdges;
        }

        public void setAllowMultiEdges(boolean allowMultiEdges) {
            this.allowMultiEdges = allowMultiEdges;
        }
    }

    private Integer nodesNum;
    private Integer layersNum;
    private Integer nodesPerLayer;
    private boolean generateBias;

    private boolean allEdges;
    private Integer inEdgesNum;
    private boolean preferentialAttachment;
    private RandomDistribution<Integer> inEdgesDistribution;
    private Integer edgesNum;
    private boolean allowSelfEdges;
    private boolean allowMultiEdges;

    // edges between consecutive layers
    private boolean intraLayerAllEdges;
    private Integer intraLayerInEdgesNum;

    // layers are assumed to partition the whole network
    private List<Topology> layers = new ArrayList<Topology>();
    private List<IntraLayerEdgeSpec> intraLayerEdgeSpecs = new ArrayList<>();

    public Integer getNodesNum() {
        return nodesNum;
    }

    public void setNodesNum(Integer nodesNum) {
        this.nodesNum = nodesNum;
    }

    public boolean isAllEdges() {
        return allEdges;
    }

    public void setAllEdges(boolean allEdges) {
        this.allEdges = allEdges;
    }

    public RandomDistribution<Integer> getInEdgesDistribution() {
        return inEdgesDistribution;
    }

    public void setInEdgesDistribution(RandomDistribution<Integer> inEdgesDistribution) {
        this.inEdgesDistribution = inEdgesDistribution;
    }

    public Integer getEdgesNum() {
        return edgesNum;
    }

    public void setEdgesNum(Integer edgesNum) {
        this.edgesNum = edgesNum;
    }

    public boolean isPreferentialAttachment() {
        return preferentialAttachment;
    }

    public void setPreferentialAttachment(boolean preferentialAttachment) {
        this.preferentialAttachment = preferentialAttachment;
    }

    public boolean isIntraLayerAllEdges() {
        return intraLayerAllEdges;
    }

    public void setIntraLayerAllEdges(boolean intraLayerAllEdges) {
        this.intraLayerAllEdges = intraLayerAllEdges;
    }

    public Integer getIntraLayerInEdgesNum() {
        return intraLayerInEdgesNum;
    }

    public void setIntraLayerInEdgesNum(Integer intraLayerInEdgesNum) {
        this.intraLayerInEdgesNum = intraLayerInEdgesNum;
    }

    public Integer getInEdgesNum() {
        return inEdgesNum;
    }

    public void setInEdgesNum(Integer inEdgesNum) {
        this.inEdgesNum = inEdgesNum;
    }

    public boolean isAllowSelfEdges() {
        return allowSelfEdges;
    }

    public void setAllowSelfEdges(boolean allowSelfEdges) {
        this.allowSelfEdges = allowSelfEdges;
    }

    public boolean isAllowMultiEdges() {
        return allowMultiEdges;
    }

    public void setAllowMultiEdges(boolean allowMultiEdges) {
        this.allowMultiEdges = allowMultiEdges;
    }

    public Integer getLayersNum() {
        return layersNum;
    }

    public void setLayersNum(Integer layersNum) {
        this.layersNum = layersNum;
    }

    public Integer getNodesPerLayer() {
        return nodesPerLayer;
    }

    public void setNodesPerLayer(Integer nodesPerLayer) {
        this.nodesPerLayer = nodesPerLayer;
    }

    @Override
    public List<Topology> getLayers() {
        return layers;
    }

    public void setLayers(List<Topology> layers) {
        this.layers = layers;
    }

    public void addLayer(Topology layer) {
        layers.add(layer);
        layer.addParent(this);
    }

    public void removeLayer(Topology layer) {
        layers.remove(layer);
        layer.removeParent(this);
    }

    public List<IntraLayerEdgeSpec> getIntraLayerEdgeSpecs() {
        return intraLayerEdgeSpecs;
    }

    public void setIntraLayerEdgeSpecs(List<IntraLayerEdgeSpec> intraLayerEdgeSpecs) {
        this.intraLayerEdgeSpecs = intraLayerEdgeSpecs;
    }

    public void addIntraLayerEdgeSpec(IntraLayerEdgeSpec spec) {
        intraLayerEdgeSpecs.add(spec);
    }

    public boolean isGenerateBias() {
        return generateBias;
    }

    public void setGenerateBias(boolean generateBias) {
        this.generateBias = generateBias;
    }

    @Override
    public boolean isTemplate() {
        return true;
    }

    @Override
    public List<TopologicalNode> getAllNodes() {
        // no nodes available
        return null;
    }

    @Override
    public boolean supportLayers() {
        return true;
    }

    @Override
    public boolean isSpatial() {
        return false;
    }
}