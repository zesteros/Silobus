
        mMarkerMovement = new MarkerMovement(mMap);
        mMapCalculator = new MapCalculator();
        mQueryRoute = new QueryRoute(mMap, this);
        while (true) {
            final Marker[] marker = new Marker[1];
            ((MainActivity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    marker[0] = mMap.addMarker(new MarkerOptions()
                            .position((LatLng) mPoints.get(0))
                            .title("Ruta: X-62-I")
                            .draggable(false)
                            //.infoWindowAnchor(40,40)
                            .snippet("distancia a user: " +
                                    mMapCalculator.getDistanceBetweenTwoCoordinates(
                                            mMapAdapter.getUserLocation(), mMapAdapter.getUserLocation()

                                    ))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)));
                    //mMap.addPolyline(mPolylineOptions.color(Color.BLACK).width(5));
                }
            });
            for (int i = 1; i < mPoints.size(); i++) {
                final int finalI = i;
                ((MainActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Log.d("POS_ROT", marker[0].getRotation() + "");
                        mMarkerMovement.moveMarker(marker[0], (LatLng) mPoints.get(finalI));
                        //mMap.moveCamera(CameraUpdateFactory.newLatLng((LatLng) mPoints.get(finalI)));
                        if (finalI + 1 < mPoints.size())
                            mMarkerMovement
                                    .rotateMarker(marker[0],
                                            mMapCalculator.getAngleBetweenTwoCoordinates
                                                    (((LatLng) mPoints.get(finalI)),
                                                            ((LatLng) mPoints.get(finalI + 1))));
                        marker[0].setSnippet("distancia a user: " +
                                mMapCalculator.getDistanceBetweenTwoCoordinates(
                                        mMapAdapter.getUserLocation(), (LatLng) mPoints.get(finalI)

                                ));
                        /*
                        *
                        * get the route and draw (add the polyline)
                        * for not clear map use polyline.setPoints(list);
                        *
                        * */
                        //mQueryRoute.getRouteBetweenTwoCoordinates(mMapAdapter.getUserLocation(), (LatLng) mPoints.get(finalI));
                    }
                });
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ((MainActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      //  mMap.clear();
                    }
                });

            }
            ((MainActivity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    marker[0].remove();
                }
            });

        }