package com.teamagam.gimelgimel.app.map.model.entities;

import com.teamagam.gimelgimel.app.map.model.entities.visitors.IEntitiesVisitor;
import com.teamagam.gimelgimel.app.map.model.geometries.MultiPointGeometry;
import com.teamagam.gimelgimel.app.map.model.symbols.PolylineSymbol;
import com.teamagam.gimelgimel.app.map.model.symbols.Symbol;

/**
 * An entity class representing a polyline
 */
public class Polyline extends MultipleLocationsEntity {

    private static final String sPolylinePrefix = "polyline";
    private PolylineSymbol mPolylineSymbol;

    protected Polyline(String id, MultiPointGeometry pointsGeometry) {
        this(id, pointsGeometry, PolylineSymbol.DEFAULT);
    }

    protected Polyline(String id, MultiPointGeometry pointsGeometry, PolylineSymbol polylineSymbol) {
        super(id, pointsGeometry);
        mPolylineSymbol = polylineSymbol;
    }

    @Override
    public Symbol getSymbol() {
        return mPolylineSymbol;
    }

    @Override
    public void updateSymbol(Symbol symbol) {
        if (!(symbol instanceof PolylineSymbol)) {
            throw new UnsupportedOperationException(
                    "Given symbol is not supported for entities of type " + Polyline.class.getSimpleName());
        }

        mPolylineSymbol = (PolylineSymbol) symbol;
        fireEntityChanged();
    }

    @Override
    public void accept(IEntitiesVisitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends MultiPointGeometryBuilder<Builder, Polyline> {

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        protected String getIdPrefix() {
            return sPolylinePrefix;
        }
        @Override
        public Polyline build() {
            return new Polyline(mId, (MultiPointGeometry) mGeometry, (PolylineSymbol) mSymbol);
        }
    }
}