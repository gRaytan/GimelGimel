package com.teamagam.gimelgimel.app.view.viewer.data.entities;

import com.teamagam.gimelgimel.app.view.viewer.IEntitiesVisitor;
import com.teamagam.gimelgimel.app.view.viewer.data.geometries.Geometry;
import com.teamagam.gimelgimel.app.view.viewer.data.geometries.PointGeometry;
import com.teamagam.gimelgimel.app.view.viewer.data.symbols.PointSymbol;
import com.teamagam.gimelgimel.app.view.viewer.data.symbols.PointTextSymbol;
import com.teamagam.gimelgimel.app.view.viewer.data.symbols.Symbol;

/**
 * An entity class representing a point
 */
public class Point extends AbsEntity<Point> {

    private PointGeometry mPointGeometry;
    private PointSymbol mPointSymbol;

    //TODO: enable instantiation via some builder-pattern that manages ids
    public Point(String id, PointGeometry pointGeometry) {
        this(id, pointGeometry, PointTextSymbol.DEFAULT);
    }

    public Point(String id, PointGeometry pointGeometry, PointSymbol pointSymbol) {
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

    public class Builder extends AbsEntity.Builder {
        public Builder(String id) {
            super(id);
        }

        @Override
        public Point.Builder setGeometry(Geometry geoometry) {
            return this;
        }

        @Override
        public Polygon.Builder setSymbol(Symbol symbol) {
            return this;
        }

        @Override
        public Polygon create() {
            return new Point(mId, mPointGeometry, mPointSymbol);
        }
    }
}
