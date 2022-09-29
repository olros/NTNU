import Helmet from 'react-helmet';

// Material UI Components
import { makeStyles } from '@material-ui/core/styles';
import { Typography, Hidden } from '@material-ui/core';

// Project Components
import Navigation from 'components/navigation/Navigation';
import Logo from 'components/miscellaneous/Logo';
import Paper from 'components/layout/Paper';

import BACKGROUND from 'assets/img/DefaultBackground.jpg';

const useStyles = makeStyles((theme) => ({
  title: {
    padding: theme.spacing(2, 0),
  },
  columns: {
    display: 'grid',
    gap: theme.spacing(2),
    gridTemplateColumns: '1fr 1fr 1fr',
    alignItems: 'self-start',
    paddingTop: theme.spacing(2),
    [theme.breakpoints.down('xl')]: {
      gridTemplateColumns: '1fr 1fr',
    },
    [theme.breakpoints.down('lg')]: {
      gridTemplateColumns: '1fr',
    },
  },
  img: {
    width: '100%',
    height: '100%',
    objectFit: 'cover',
    borderRadius: theme.shape.borderRadius,
  },
}));

const About = () => {
  const classes = useStyles();

  return (
    <Navigation topbarVariant='dynamic'>
      <Helmet>
        <title>Om GIDD</title>
      </Helmet>
      <Typography className={classes.title} variant='h1'>
        Om GIDD
      </Typography>
      <div className={classes.columns}>
        <Paper>
          <Typography gutterBottom variant='h3'>
            Hva er GIDD?
          </Typography>
          <Typography gutterBottom>
            {`GIDD er rett og slett dummitekst fra trykk- og settebransjen. GIDD har vært bransjens standard dummytekst helt siden
             1500-tallet, da en ukjent skriver tok en bysse av typen og krypterte den for å lage en type eksemplarbok. Den har overlevd ikke bare fem århundrer, men
             også spranget til elektronisk setting, og forblir i hovedsak uendret. Den ble popularisert på 1960-tallet med utgivelsen av Letraset-ark
             som inneholder GIDD-passasjer, og nylig med desktop publishing-programvare som Aldus PageMaker inkludert versjoner av GIDD.`}
          </Typography>
          <Logo darkColor={'white'} lightColor={'black'} />
        </Paper>
        <Paper>
          <Typography gutterBottom variant='h3'>
            Historien vår
          </Typography>
          <Typography gutterBottom>
            {`I motsetning til hva mange tror er GIDD ikke bare tilfeldig tekst. Den har røtter i et stykke klassisk latinsk litteratur fra 45 f.Kr.,
            som gjør den over 2000 år gammel. Richard McClintock, en latinprofessor ved Hampden-Sydney College i Virginia, slo opp et av de mer uklare latinske ordene,
            consectetur, fra en passasje fra GIDD, og oppdaget den utvilsomme kilden gjennom ordets sitater i klassisk litteratur. GIDD kommer fra
            seksjonene 1.10.32 og 1.10.33 i "de Finibus Bonorum et Malorum" (The Extremes of Good and Evil) av Cicero, skrevet i 45 f.Kr. Denne boken er en avhandling om
            teorien om etikk, veldig populær under renessansen. Den første linjen i GIDD, "Lorem ipsum dolor sit amet ..", kommer fra en linje i avsnitt 1.10.32.
            Standardbiten av GIDD som ble brukt siden 1500-tallet er gjengitt nedenfor for interesserte. Avsnitt 1.10.32 og 1.10.33 fra "de Finibus Bonorum et Malorum"
            av Cicero er også gjengitt i sin eksakte opprinnelige form, ledsaget av engelske versjoner fra 1914-oversettelsen av H. Rackham.`}
          </Typography>
        </Paper>
        <Hidden xlDown>
          <img className={classes.img} src={BACKGROUND} />
        </Hidden>
      </div>
    </Navigation>
  );
};

export default About;
