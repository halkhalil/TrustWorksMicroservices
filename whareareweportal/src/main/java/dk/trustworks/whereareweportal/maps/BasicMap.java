package dk.trustworks.whereareweportal.maps;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.*;
import org.vaadin.addon.vol3.OLMap;
import org.vaadin.addon.vol3.OLMapOptions;
import org.vaadin.addon.vol3.OLView;
import org.vaadin.addon.vol3.OLViewOptions;
import org.vaadin.addon.vol3.client.OLCoordinate;
import org.vaadin.addon.vol3.client.OLExtent;
import org.vaadin.addon.vol3.client.Projections;
import org.vaadin.addon.vol3.client.style.OLIconStyle;
import org.vaadin.addon.vol3.client.style.OLStyle;
import org.vaadin.addon.vol3.feature.OLFeature;
import org.vaadin.addon.vol3.feature.OLPoint;
import org.vaadin.addon.vol3.layer.OLLayer;
import org.vaadin.addon.vol3.layer.OLTileLayer;
import org.vaadin.addon.vol3.layer.OLVectorLayer;
import org.vaadin.addon.vol3.source.OLOSMSource;
import org.vaadin.addon.vol3.source.OLSource;
import org.vaadin.addon.vol3.source.OLVectorSource;
import org.vaadin.addon.vol3.source.OLVectorSourceOptions;
import org.vaadin.addon.vol3.util.SimpleContextMenu;

import java.io.File;
import java.util.logging.Logger;

/**
 * Created by hans on 27/12/2016.
 */
public class BasicMap extends VerticalLayout implements View {

    protected OLMap map;
    private static final Logger logger= Logger.getLogger(BasicMap.class.getName());

    public BasicMap() {
        this.setSizeFull();
        map=createMap();
        createContextMenu();
        OLVectorSourceOptions vectorOptions=new OLVectorSourceOptions();
        OLVectorSource vectorSource=new OLVectorSource(vectorOptions);
        vectorSource.addFeature(createPointFeature("AP Pension",12.589604,55.707043, "appension", "hans.lassen", "nikolaj.birch", "lars.albert"));
        vectorSource.addFeature(createPointFeature("DONG Energi",12.5161887,55.7578284, "dong.energi", "thomas.gammelvind", "gisla.faber"));
        vectorSource.addFeature(createPointFeature("Banedanmark",12.593281,55.699177, "banedanmark", "tommy.soerensen"));
        vectorSource.addFeature(createPointFeature("Kriminalforsorgen",12.5964017,55.676719, "kriminalforsorgen", "paula.hoiby"));
        vectorSource.addFeature(createPointFeature("Kombit",12.5779859,55.6642418, "kombit", "peter.gaarde"));
        //
        OLVectorLayer vectorLayer=new OLVectorLayer(vectorSource);
        vectorLayer.setLayerVisible(true);
        map.addLayer(vectorLayer);

        this.addViewChangeListener();
        this.addComponent(map);
        this.setExpandRatio(this.iterator().next(),1.0f);
        this.addComponent(createControls());
        addViewChangeListener();
        resetView();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }

    protected OLMap createMap(){
        OLMap map=new OLMap(new OLMapOptions().setShowOl3Logo(true).setInputProjection(Projections.EPSG4326));
        OLLayer layer=createLayer(createSource());
        layer.setTitle("MapQuest OSM");
        map.addLayer(layer);
        map.setView(createView());
        map.setSizeFull();
        map.addClickListener(new OLMap.ClickListener() {
            @Override
            public void onClick(OLMap.OLClickEvent clickEvent) {
                System.out.println(clickEvent.toString());
            }
        });
        return map;
    }

    private void addViewChangeListener() {
        map.getView().addViewChangeListener(new OLView.ViewChangeListener() {
            @Override
            public void resolutionChanged(Double newResolution) {
                logger.info("resolution changed " + newResolution);
            }

            @Override
            public void rotationChanged(Double rotation) {
                logger.info("rotation changed " + rotation);
            }

            @Override
            public void centerChanged(OLCoordinate centerPoint) {
                logger.info("center changed " + centerPoint.toString());
            }

            @Override
            public void zoomChanged(Integer zoom) {
                logger.info("zoom changed " + zoom);
            }

            @Override
            public void extentChanged(OLExtent extent) {
                logger.info("extent changed " + extent.minX + "," + extent.minY + "," + extent.maxX + "," + extent.maxY);
            }
        });
    }

    protected OLSource createSource(){
        return new OLOSMSource();
    }

    protected OLLayer createLayer(OLSource source){
        return new OLTileLayer(source);
    }

    protected OLView createView(){
        OLViewOptions opts=new OLViewOptions();
        opts.setInputProjection(Projections.EPSG4326);
        OLView view=new OLView(opts);
        view.setZoom(1);
        view.setCenter(0,0);
        return view;
    }

    protected void resetView(){
        map.getView().setCenter(12.464332580566406,55.6930679264579);
        map.getView().setZoom(12);
    }

    protected OLFeature createPointFeatureParent(String id, double x, double y){
        OLFeature testFeature=new OLFeature(id);
        testFeature.setStyle(new OLStyle());
        testFeature.setGeometry(new OLPoint(x,y));
        return testFeature;
    }

    protected OLFeature createPointFeature(String id, double x, double y, String company, String... employees) {
        OLFeature pointFeature = createPointFeatureParent(id, x, y);
        OLStyle style= new OLStyle();
        style.iconStyle=new OLIconStyle();
        //style.iconStyle.size=new double[]{100.0,50.0};
        //style.iconStyle.src = "VAADIN/img/flag.png";

        String src = "/logo?company="+company;
        for (String employee : employees) {
            src = src + "&employees="+employee;
        }

        style.iconStyle.src = src;
        pointFeature.setStyle(style);
        return pointFeature;
    }


    protected AbstractLayout createControls() {
        HorizontalLayout controls=new HorizontalLayout();
        controls.setSpacing(true);
        controls.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        Button button=new Button("Reset view");
        button.addClickListener((Button.ClickListener) event -> resetView());
        controls.addComponent(button);
        button=new Button("Show view state");
        button.addClickListener((Button.ClickListener) event -> {
            OLCoordinate center=map.getView().getCenter();
            StringBuilder message=new StringBuilder();
            message.append("center: ").append(center.toString()).append("\n");
            message.append("rotation: ").append(map.getView().getRotation()).append("\n");
            message.append("zoom: ").append(map.getView().getZoom()).append("\n");
            message.append("resolution: ").append(map.getView().getResolution()).append("\n");
            Notification.show(message.toString());
        });
        controls.addComponent(button);
        button=new Button("Toggle map visibility");
        button.addClickListener((Button.ClickListener) event -> map.setVisible(!map.isVisible()));
        controls.addComponent(button);
        button=new Button("Fit extent");
        button.addClickListener((Button.ClickListener) event -> map.getView().fitExtent(createExtent()));
        controls.addComponent(button);
        return controls;
    }


    protected OLExtent createExtent(){
        OLExtent extent=new OLExtent();
        extent.minX=19.0;
        extent.maxX=32.0;
        extent.minY=60.0;
        extent.maxY=70.0;
        return extent;
    }

    protected void createContextMenu(){
        SimpleContextMenu simpleContextMenu = new SimpleContextMenu(map);
        simpleContextMenu.addItem("Test context menu item", () -> Notification.show("context item clicked"));
    }

}