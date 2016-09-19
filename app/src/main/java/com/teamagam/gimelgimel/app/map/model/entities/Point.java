package com.teamagam.gimelgimel.app.map.model.entities;

import com.teamagam.gimelgimel.app.map.model.entities.visitors.IEntitiesVisitor;
import com.teamagam.gimelgimel.app.map.model.geometries.Geometry;
import com.teamagam.gimelgimel.app.map.model.geometries.PointGeometry;
import com.teamagam.gimelgimel.app.map.model.symbols.PointSymbol;
import com.teamagam.gimelgimel.app.map.model.symbols.PointTextSymbol;
import com.teamagam.gimelgimel.app.map.model.symbols.Symbol;

/**
 * An entity class representing a point
 */
public class Point extends AbsEntity {

    private static final String sPointPrefix = "point";
    private PointGeometry mPointGeometry;
    private PointSymbol mPointSymbol;

    protected Point(String id, PointGeometry pointGeometry) {
        this(id, pointGeometry, PointTextSymbol.DEFAULT);
    }

    protected Point(String id, PointGeometry pointGeometry, PointSymbol pointSymbol) {
        super(id);
        mPointGeometry = pointGeometry;
        mPointSymbol = pointSymbol;
    }

    @Override
    public Geometry getGeometry() {
        return mPointGeometry;
    }

    @Override
    public Symbol getSymbol() {
        return mPointSymbol;
    }

    @Override
    public void updateGeometry(Geometry geo) {
        if (!(geo instanceof PointGeometry)) {
            throw new UnsupportedOperationException(
                    "Given geometry is not supported for entities of type " + Point.class.getSimpleName());
        }

        mPointGeometry = (PointGeometry) geo;
        fireEntityChanged();
    }

    @Override
    public void updateSymbol(Symbol symbol) {
        if (!(symbol instanceof PointSymbol)) {
            throw new UnsupportedOperationException(
                    "Given symbol is not supported for entities of type " + Point.class.getSimpleName());
        }

        mPointSymbol = (PointSymbol) symbol;
        fireEntityChanged();
    }

    @Override
    public void accept(IEntitiesVisitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends EntityBuilder<Builder, Point> {

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        protected String getIdPrefix() {
            return sPointPrefix;
        }

        @Override
        public Point build() {
            return new Point(mId, (PointGeometry) mGeometry, (PointSymbol) mSymbol);
        }
    }
}