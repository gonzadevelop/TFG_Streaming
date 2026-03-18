package tfg.KeySound.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tfg.KeySound.entitys.Album;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
}

