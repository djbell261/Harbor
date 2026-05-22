import 'leaflet/dist/leaflet.css';
import L from 'leaflet';
import { MapContainer, Marker, Popup, TileLayer } from 'react-leaflet';
import { Button } from '../../components/Button';
import { Card } from '../../components/Card';
import type { ResourceSummary } from '../../types/resource';

interface ResourceMapInnerProps {
  resources: ResourceSummary[];
  onOpenResource: (id: string) => void;
}

export function ResourceMapInner({ resources, onOpenResource }: ResourceMapInnerProps) {
  const mappedResources = resources.filter(
    (resource) => resource.latitude !== null && resource.longitude !== null
  );

  if (mappedResources.length === 0) {
    return (
      <Card>
        <p className="text-sm text-harbor-muted">No mappable resources are available for this filter.</p>
      </Card>
    );
  }

  const center: [number, number] = [
    Number(mappedResources[0].latitude),
    Number(mappedResources[0].longitude)
  ];
  const markerIcon = L.divIcon({
    className: 'harbor-map-marker',
    html: '<span></span>',
    iconSize: [18, 18],
    iconAnchor: [9, 9]
  });

  return (
    <div
      className="h-[70vh] min-h-[20rem] max-h-[28rem] overflow-hidden border border-harbor-line bg-white shadow-soft"
      aria-label="Resource map"
    >
      <MapContainer center={center} zoom={13} className="h-full w-full" scrollWheelZoom={false}>
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        {mappedResources.map((resource) => (
          <Marker
            key={resource.id}
            icon={markerIcon}
            position={[Number(resource.latitude), Number(resource.longitude)]}
          >
            <Popup>
              <div className="space-y-2">
                <p className="font-semibold">{resource.name}</p>
                <p>{resource.categoryName}</p>
                <Button onClick={() => onOpenResource(resource.id)}>View details</Button>
              </div>
            </Popup>
          </Marker>
        ))}
      </MapContainer>
    </div>
  );
}
